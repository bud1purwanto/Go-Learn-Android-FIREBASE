package com.budi.go_learn.Activity.Pengajar;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budi.go_learn.Activity.Public.LoginPengajar;
import com.budi.go_learn.Activity.User.ProfilUser;
import com.budi.go_learn.Activity.User.UserActivity;
import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Controller.SessionManager;
import com.budi.go_learn.Models.mHistori;
import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.Models.mTransaksi;
import com.budi.go_learn.R;
import com.budi.go_learn.Variable.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PengajarActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        View.OnLongClickListener{

    public static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_PHONE_CALL = 1;
    private TextView txtName, txtEmail, txtStatus;
    private ImageView imgProfil;
    private ConstraintLayout Header;
    private SQLiteHandler db;
    private SessionManager session;
    private String name, email, uid, namaUser, genderUser, alamatUser, urlUser, emailUser, phoneUser, text;
    private String id_pengajar, id_user, created_at, namePengajar, emailPengajar, phonePengajar, genderPengajar, addressPengajar, districtPengajar, cityPengajar, pelajaranPengajar, descriptionPengajar, urlPengajar;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseUser firebaseLogin;
    private DatabaseReference databaseReference, databasePengajar, databaseHistori;
    private ImageView imgStatusTransaksi, imgUser;
    private TextView txtStatusTransaksi, txtNamaUser,txtGenderUser, txtAddressUser, txtNoOrder;
    private Button btnSelesai;
    private ProgressDialog pDialog;
    private FloatingActionButton map, hubungi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajar);
        setTitle("Halaman Pengajar");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseLogin = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("transaksi");
        databasePengajar = FirebaseDatabase.getInstance().getReference("pengajar");
        databaseHistori = FirebaseDatabase.getInstance().getReference("histori");
        storageReference = FirebaseStorage.getInstance().getReference();
        uid = firebaseAuth.getUid();

        imgStatusTransaksi = findViewById(R.id.imgStatusTransaksi);
        imgUser = findViewById(R.id.imgUser);
        txtStatusTransaksi = findViewById(R.id.txtStatusTransaksi);
        txtNamaUser = findViewById(R.id.txtNamaUser);
        txtGenderUser = findViewById(R.id.txtGenderUser);
        txtAddressUser = findViewById(R.id.txtAddressUser);
        txtNoOrder = findViewById(R.id.txtNoOrder);
        btnSelesai = findViewById(R.id.btnSelesai);
        map = findViewById(R.id.map);
        hubungi = findViewById(R.id.hubungi);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        txtName = header.findViewById(R.id.namaProfil);
        txtEmail = header.findViewById(R.id.emailProfil);
        txtStatus = header.findViewById(R.id.statusProfil);
        imgProfil = header.findViewById(R.id.fotoProfil);
        Header = header.findViewById(R.id.header);

        Header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfilPengajar.class);
                startActivity(i);

            }
        });

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);

        if (!session.isLoggedIn() && firebaseAuth.getCurrentUser() == null) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();

        name = user.get(User.KEY_NAME);
        email = user.get(User.KEY_EMAIL);

        txtName.setText(name);
        txtEmail.setText(email);
        txtStatus.setText("Pengajar");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        map.setOnClickListener(this);
        hubungi.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);
        btnSelesai.setOnLongClickListener(this);
        retrieveImage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTransaksi();
        dataPengajar();
    }


    @Override
    public void onClick(View view) {
        if (view == map){
            startActivity(new Intent(getApplicationContext(), MapsPengajar.class).putExtra(User.KEY_ADDRESS, alamatUser));
        }
        if (view == btnSelesai){
            KonfirmasiSelesaiOrder();
        }
        if (view == hubungi) {
            Intent i = new Intent(PengajarActivity.this, DirectChatP.class);
            i.putExtra("id_user", id_user);
            i.putExtra("id_pengajar", firebaseLogin.getUid());
            i.putExtra("nama_pengajar", name);
            i.putExtra("nama_user", namaUser);
            startActivity(i);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == hubungi){
            PopupMenu popup = new PopupMenu(PengajarActivity.this, hubungi);
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

    @Override
    protected void onPause() {
        super.onPause();
        getTransaksi();
    }

    public void tampilNotification() {

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.computerdrift.blogspot.com/"));
        Intent intent = new Intent(getApplicationContext(), ProfilUser.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.golearn2)
                .setContentTitle("Notifikasi Baru")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(12)
                .setContentText("Kunjungi Blog Saya");


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build()
        );
    }

    public void getTransaksi(){
        pDialog.setMessage("Loading");
        showDialog();
        databaseReference.orderByChild("id_pengajar").equalTo(firebaseLogin.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot transaksi : dataSnapshot.getChildren()){
                            mTransaksi lP = transaksi.getValue(mTransaksi.class);
                            id_user = lP.getId_user();
                            namaUser = lP.getNameU();
                            genderUser = lP.getGenderU();
                            alamatUser = lP.getAddressU();
                            urlUser = lP.getUrlU();
                            emailUser = lP.getEmailU();
                            phoneUser = lP.getPhoneU();
                            created_at = lP.getCreated_at();

                            txtNamaUser.setText(namaUser);
                            txtGenderUser.setText(genderUser);
                            txtAddressUser.setText(alamatUser);

                            Glide.with(getApplicationContext()).load(urlUser).apply(RequestOptions.circleCropTransform()).into(imgUser);
                        }

                        if (emailUser != null){
                            imgStatusTransaksi.setImageResource(R.drawable.statusgreen);
                            txtStatusTransaksi.setText("Transaksi Aktif");
                            imgUser.setVisibility(View.VISIBLE);
                            txtNamaUser.setVisibility(View.VISIBLE);
                            txtGenderUser.setVisibility(View.VISIBLE);
                            txtAddressUser.setVisibility(View.VISIBLE);
                            btnSelesai.setVisibility(View.VISIBLE);
                            map.setVisibility(View.VISIBLE);
                            hubungi.setVisibility(View.VISIBLE);
                            txtNoOrder.setVisibility(View.GONE);

                        } else if(emailUser == null){
                            imgStatusTransaksi.setImageResource(R.drawable.statusred);
                            txtStatusTransaksi.setText("Transaksi Nonaktif");
                            imgUser.setVisibility(View.GONE);
                            txtNamaUser.setVisibility(View.GONE);
                            txtGenderUser.setVisibility(View.GONE);
                            txtAddressUser.setVisibility(View.GONE);
                            btnSelesai.setVisibility(View.GONE);
                            map.setVisibility(View.GONE);
                            hubungi.setVisibility(View.GONE);
                            txtNoOrder.setVisibility(View.VISIBLE);
                        }

                        hideDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void dataPengajar(){
        databasePengajar.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            mPengajar lP = dataSnapshot.getValue(mPengajar.class);
                            id_pengajar = lP.getUid();
                            namePengajar = lP.getName();
                            emailPengajar = lP.getEmail();
                            phonePengajar = lP.getPhone();
                            genderPengajar = lP.getGender();
                            addressPengajar = lP.getAddress();
                            districtPengajar = lP.getDistrict();
                            cityPengajar = lP.getCity();
                            pelajaranPengajar = lP.getPelajaran();
                            descriptionPengajar = lP.getKeterangan();
                            urlPengajar = lP.getUrl();
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void selesaiOrder(){
        mTransaksi transaksi = new mTransaksi();
        databaseReference.child(uid).setValue(transaksi);
        editStatusPengajar();
        addHitsori();
        finish();
        startActivity(new Intent(getApplicationContext(), PengajarActivity.class));
        Toast.makeText(getApplicationContext(), "Order Selesai", Toast.LENGTH_SHORT).show();
    }

    private void KonfirmasiSelesaiOrder(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sudah Selesai Mengajar?");

        alertDialogBuilder.setPositiveButton("Selesai",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        showDialog();
                        selesaiOrder();
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
        mPengajar pengajar = new mPengajar(uid, namePengajar, emailPengajar, phonePengajar,
                genderPengajar, addressPengajar, districtPengajar, cityPengajar, pelajaranPengajar, descriptionPengajar,
                "pengajar", "1", "0", urlPengajar);
        databasePengajar.child(uid).setValue(pengajar);
    }

    private void addHitsori(){
        String id = databaseHistori.push().getKey();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        mHistori histori = new mHistori(id, id_pengajar, id_user, created_at, dateFormat.format(date),
                namePengajar, emailPengajar, phonePengajar, genderPengajar, addressPengajar,districtPengajar,
                cityPengajar, pelajaranPengajar, descriptionPengajar, urlPengajar, namaUser, emailUser, phoneUser, genderUser, alamatUser, urlUser);
        databaseHistori.child(id).setValue(histori);
        editStatusPengajar();
        finish();
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
        Toast.makeText(getApplicationContext(), "Sukses Order", Toast.LENGTH_SHORT).show();
    }

    public void retrieveImage(){
       try {
           storageReference.child("pengajar/"+uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {
                   loadImage(uri);
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception exception) {
               }
           });
       } catch (Exception e){
           e.printStackTrace();
       }
    }

    public void loadImage(Uri uri){
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(imgProfil);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveImage();
        getTransaksi();
        HashMap<String, String> user = db.getUserDetails();

        name = user.get(User.KEY_NAME);
        email = user.get(User.KEY_EMAIL);

        txtName.setText(name);
        txtEmail.setText(email);
        txtStatus.setText("Pengajar");
    }

    public void cCall(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL); //ACTION_CALL
        callIntent.setData(Uri.parse("tel:"+phoneUser+""));
        if (ContextCompat.checkSelfPermission(PengajarActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PengajarActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        } else {
            startActivity(callIntent);
        }
    }

    public void cSms(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneUser));
        intent.putExtra("sms_body", text);
        startActivity(intent);
    }

    public void cWA(){
        String str = phoneUser;
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

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        firebaseAuth.signOut();

        Intent intent = new Intent(PengajarActivity.this, LoginPengajar.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingPengajar.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void KonfirmasiLogout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Anda ingin logout?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Logout",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        logoutUser();
                    }
                });

        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.histori) {
            Intent i = new Intent(PengajarActivity.this, HistoriPengajar.class);
            startActivity(i);
        }
        if (id == R.id.pesan) {
            Intent i = new Intent(PengajarActivity.this, PilihPesanP.class);
            i.putExtra("nama_user", name);
            startActivity(i);
        } else if (id == R.id.editprofile) {
            Intent i = new Intent(PengajarActivity.this, ProfilPengajar.class);
            startActivity(i);
        } else if (id == R.id.editpassword) {
            Intent i = new Intent(PengajarActivity.this, ChangePasswordP.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            KonfirmasiLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
