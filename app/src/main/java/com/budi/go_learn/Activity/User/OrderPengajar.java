package com.budi.go_learn.Activity.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.Models.mTransaksi;
import com.budi.go_learn.Models.mUser;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class OrderPengajar extends AppCompatActivity implements
        View.OnClickListener,
        View.OnLongClickListener{
    private static final int REQUEST_PHONE_CALL = 1;
    private String idPengajar, name, email, phone, address, pelajaran, description, district, city, gender, active, work, url, urlUser;
    private TextView txtEmail, txtPhone, txtAddress, txtPelajaran, txtDescription, txtStatus, txtGender;
    private ImageView imgOrderPengajar, imgEmail, imgPhone;
    private ProgressDialog pDialog;
    private Button btnContact;
    private SQLiteHandler db;
    private String nama, telp, imel, kel, alamat, salam, uiduser, text;
    private PopupMenu popup;
    private FloatingActionButton map, order;
    private ProgressDialog loading;
    private DatabaseReference databaseTransaksi, databasePengajar, databaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pengajar);

        firebaseAuth =FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseTransaksi = FirebaseDatabase.getInstance().getReference("transaksi");
        databasePengajar = FirebaseDatabase.getInstance().getReference("pengajar");
        databaseUser = FirebaseDatabase.getInstance().getReference("user");

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
        pDialog.setCancelable(false);

        Intent i = getIntent();
        idPengajar = i.getStringExtra(Pengajar.KEY_UID);
        name = i.getStringExtra(Pengajar.KEY_NAME);
        email = i.getStringExtra(Pengajar.KEY_EMAIL);
        phone = i.getStringExtra(Pengajar.KEY_PHONE);
        gender = i.getStringExtra(Pengajar.KEY_GENDER);
        address = i.getStringExtra(Pengajar.KEY_ADDRESS);
        district = i.getStringExtra(Pengajar.KEY_DISTRICT);
        city = i.getStringExtra(Pengajar.KEY_CITY);
        pelajaran = i.getStringExtra(Pengajar.KEY_PELAJARAN);
        description = i.getStringExtra(Pengajar.KEY_KET);
        active = i.getStringExtra(Pengajar.KEY_ACTIVE);
        work = i.getStringExtra(Pengajar.KEY_WORK);
        url = i.getStringExtra(Pengajar.KEY_URL);

        setTitle(name);
        txtEmail.setText(email);
        txtPhone.setText(phone);
        txtGender.setText(gender);
        txtAddress.setText(city);
        txtPelajaran.setText(pelajaran);
        txtDescription.setText(description);

        HashMap<String, String> user = db.getUserDetails();
        uiduser = user.get(User.KEY_UID);
        nama = user.get(User.KEY_NAME);
        telp = user.get(User.KEY_PHONE);
        imel = user.get(User.KEY_EMAIL);
        kel = user.get(User.KEY_GENDER);
        alamat = user.get(User.KEY_ADDRESS);

        if (gender==null || gender.equals("Laki-laki")){
            salam = "Bapak";
        } else {
            salam = "Ibu";
        }

        text = "Permisi "+salam+" "+name+", \nSaya "+nama+" ingin memesan jasa les Anda. " +
                "\n\nSaya bisa dihubungi melalui: \nTelepon      : "+telp+"\nEmail    : "+imel;

        map = findViewById(R.id.mapuser);
        order = findViewById(R.id.order);
        map.setOnClickListener(this);
        order.setOnClickListener(this);
        btnContact.setOnLongClickListener(this);


        if (active==null || active.equals("0") || work.equals("1")){
            order.setVisibility(View.GONE);
        }

        if (address==null){
            map.setVisibility(View.GONE);
        }

        order.setVisibility(View.GONE);
        haveTransaction();
        urlUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(url != null){
            Glide.with(this).load(url).into(imgOrderPengajar);
        }
    }

    public void urlUser(){
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    mUser data = user.getValue(mUser.class);
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    if(u.getEmail().equals(data.getEmail())){
                        urlUser = data.getUrl();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void haveTransaction(){
        databaseTransaksi.orderByChild("id_user").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTransaksi data = dataSnapshot.getValue(mTransaksi.class);
                        if (data != null){
                            order.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Transaksi Anda masih berjalan!", Toast.LENGTH_SHORT).show();
                        } else if (data == null){
                            order.setVisibility(View.VISIBLE);
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

            Intent i = new Intent(OrderPengajar.this, MapsUser.class);
            i.putExtra(Pengajar.KEY_ADDRESS, address+", "+district+", "+city);
            startActivity(i);
        }
        if (view == order) {
            KonfirmasiOrder();
        }
        if (view == btnContact) {
            Intent i = new Intent(OrderPengajar.this, DirectChatU.class);
            i.putExtra("id_user", uiduser);
            i.putExtra("id_pengajar", idPengajar);
            i.putExtra("nama_pengajar", name);
            i.putExtra("nama_user", nama);
            startActivity(i);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == btnContact){
            popup = new PopupMenu(OrderPengajar.this, btnContact);
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
        if (ContextCompat.checkSelfPermission(OrderPengajar.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OrderPengajar.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
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

    private void Order(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        mTransaksi transaksi = new mTransaksi(idPengajar, idPengajar, uiduser, dateFormat.format(date),
                name, email, phone, gender, address,district, city, pelajaran, description, url, nama, imel, telp, kel, alamat, urlUser);
        databaseTransaksi.child(idPengajar).setValue(transaksi);
        editStatusPengajar();
        finish();
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
        Toast.makeText(getApplicationContext(), "Sukses Order", Toast.LENGTH_SHORT).show();
    }

    private void KonfirmasiOrder(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Kamu Yakin Ingin Order Pengajar?");

        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        showDialog();
                        Order();
                        order.setVisibility(View.GONE);
                        hideDialog();
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void editStatusPengajar(){
        mPengajar pengajar = new mPengajar(idPengajar, name, email, phone, gender, address, district, city, pelajaran, description, "pengajar", "1", "1", url);
        databasePengajar.child(idPengajar).setValue(pengajar);
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
