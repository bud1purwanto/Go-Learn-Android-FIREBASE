package com.budi.go_learn.Activity.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Models.mTransaksi;
import com.budi.go_learn.R;
import com.budi.go_learn.Variable.Pengajar;
import com.budi.go_learn.Variable.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ListOrder extends AppCompatActivity implements
        View.OnClickListener,
        View.OnLongClickListener{
    private static final String TAG = ListOrder.class.getSimpleName();
    private static final int REQUEST_PHONE_CALL = 1;
    private String id, id_user, id_pengajar, name, email, phone, address, district, city, pelajaran, description, gender, active, work, url;
    private String nama, telp, imel, salam, text;
    private TextView txtEmail, txtPhone, txtAddress, txtPelajaran, txtDescription, txtStatus, txtGender;
    private ImageView imgOrderPengajar, imgEmail, imgPhone;
    private ProgressDialog pDialog;
    private Button btnContact;
    private SQLiteHandler db;
    private PopupMenu popup;
    private FloatingActionButton map;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("transaksi");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnContact = findViewById(R.id.btnContact);

        imgOrderPengajar = findViewById(R.id.imgOrderPengajar);
        imgEmail = findViewById(R.id.imgEmail);
        imgPhone = findViewById(R.id.imgPhone);

        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtGender = findViewById(R.id.txtGender);
        txtAddress = findViewById(R.id.txtAddress);
        txtPelajaran = findViewById(R.id.txtPelajaran);
        txtStatus = findViewById(R.id.txtStatusTransaksi);
        txtDescription = findViewById(R.id.txtDescription);

        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        txtEmail.setOnClickListener(this);
        txtPhone.setOnClickListener(this);

        db = new SQLiteHandler(getApplicationContext());
        pDialog = new ProgressDialog(this);

        Intent i = getIntent();
        email = i.getStringExtra(Pengajar.KEY_EMAIL);

        HashMap<String, String> user = db.getUserDetails();
        nama = user.get(User.KEY_NAME);
        telp = user.get(User.KEY_PHONE);
        imel = user.get(User.KEY_EMAIL);

        setTitle(name);

        map = findViewById(R.id.mapuser);
        map.setOnClickListener(this);
        btnContact.setOnLongClickListener(this);


        text = "Permisi "+salam+" "+name+", \nSaya "+nama+" ingin memesan jasa les Anda. " +
                "\n\nSaya bisa dihubungi melalui: \nTelepon      : "+telp+"\nEmail    : "+imel;

    }

    @Override
    protected void onStart() {
        super.onStart();
        pDialog.setMessage("Loading");
        showDialog();
        databaseReference.orderByChild("id_user").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot transaksi : dataSnapshot.getChildren()){
                            mTransaksi lP = transaksi.getValue(mTransaksi.class);
                            id_user = lP.getId_user();
                            id_pengajar = lP.getId_pengajar();
                            name = lP.getNameP();
                            email = lP.getEmailP();
                            phone = lP.getPhoneP();
                            gender = lP.getGenderP();
                            address = lP.getAddressP();
                            district = lP.getDistrictP();
                            city = lP.getCityP();
                            pelajaran = lP.getPelajaranP();
                            description = lP.getDescriptionP();
                            url = lP.getUrlP();
                            setTitle(name);

                            txtEmail.setText(email);
                            txtPhone.setText(phone);
                            txtGender.setText(gender);
                            txtAddress.setText(city);
                            txtPelajaran.setText(pelajaran);
                            txtDescription.setText(description);
                            if (gender.equals("Laki-laki")){
                                salam = "Bapak";
                            } else {
                                salam = "Ibu";
                            }
                            Glide.with(getApplicationContext()).load(url).into(imgOrderPengajar);
                        }
                        hideDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == map) {
            Intent i = new Intent(ListOrder.this, MapsUser.class);
            i.putExtra(Pengajar.KEY_ADDRESS, address);
            startActivity(i);
        }
        if (view == btnContact) {
            Intent i = new Intent(ListOrder.this, DirectChatU.class);
            i.putExtra("id_user", id_user);
            i.putExtra("id_pengajar", id_pengajar);
            i.putExtra("nama_pengajar", name);
            i.putExtra("nama_user", nama);
            startActivity(i);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == btnContact){
            popup = new PopupMenu(ListOrder.this, btnContact);
            popup.getMenuInflater().inflate(R.menu.menu_call, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id){
                        case R.id.call:
                            cCall();
                            break;
                        case R.id.sms:
                            cSms();
                            break;
                        case R.id.wa:
                            cWA();
                            break;
                        case R.id.email:
                            cEmail();
                            break;
                    }
                    return true;
                }
            });
            popup.show();
        }
        return false;
    }

    public void cCall(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL); //ACTION_CALL
        callIntent.setData(Uri.parse("tel:"+phone+""));
        if (ContextCompat.checkSelfPermission(ListOrder.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListOrder.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        } else {
            startActivity(callIntent);
        }
    }

    public void cSms(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
        intent.putExtra("sms_body", text);
        startActivity(intent);
    }

    public void cWA(){
        String str = phone;
        str = "+62"+str.substring(1);

        Intent whatsapp = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send?phone="+str+"&text="+text+""));
        startActivity(whatsapp);
    }

    public void cEmail(){
        Intent emailIntent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" +email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pemesanan Pengajar");
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(emailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(share);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

