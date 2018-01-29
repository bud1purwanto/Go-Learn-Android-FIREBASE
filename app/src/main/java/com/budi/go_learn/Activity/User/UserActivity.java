package com.budi.go_learn.Activity.User;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budi.go_learn.Activity.Public.LoginActivity;
import com.budi.go_learn.Adapter.ChatUAdapter;
import com.budi.go_learn.Adapter.MenuUserAdapter;
import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Controller.SessionManager;
import com.budi.go_learn.Features.GPSTracker;
import com.budi.go_learn.Models.mChat;
import com.budi.go_learn.Models.mFitur;
import com.budi.go_learn.Models.mTransaksi;
import com.budi.go_learn.R;
import com.budi.go_learn.RecyclerView.ClickListener;
import com.budi.go_learn.RecyclerView.RecyclerTouchListener;
import com.budi.go_learn.Variable.Pengajar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView txtName, txtEmail, txtStatus;
    private Button btnSearch;
    private ImageView imgProfil;
    private ConstraintLayout Header;
    private SQLiteHandler db;
    private SessionManager session;
    private String name, email, uid;
    private RecyclerView recyclerview1, recyclerview2;
    private MenuUserAdapter adapter1;
    private ChatUAdapter adapter2;
    private List<mFitur> menuList = new ArrayList<>();
    private List<mChat> pesanList = new ArrayList<>();
    private static final int TIME_INTERVAL = 3000;
    private long mBackPressed;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseUser firebaseLogin;
    private DatabaseReference databaseReference, root;
    private ProgressDialog pDialog;
    private GPSTracker gpsTracker;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    btnSearch.setVisibility(View.VISIBLE);
                    recyclerview1.setVisibility(View.VISIBLE);
                    recyclerview2.setVisibility(View.GONE);
                    Menu();
                    return true;
                case R.id.navigation_pesan:
                    btnSearch.setVisibility(View.GONE);
                    recyclerview1.setVisibility(View.GONE);
                    recyclerview2.setVisibility(View.VISIBLE);
                    Pesan();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("Halaman User");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseLogin = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        uid = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("transaksi");
        root = FirebaseDatabase.getInstance().getReference().child("chat");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        gpsTracker = new GPSTracker(this);

        if (!session.isLoggedIn() && firebaseAuth.getCurrentUser() == null) {
            logoutUser();
        }

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        recyclerview1 = (RecyclerView) findViewById(R.id.rvHome);
        recyclerview2 = (RecyclerView) findViewById(R.id.rvPesan);

        txtName = header.findViewById(R.id.namaProfil);
        txtEmail = header.findViewById(R.id.emailProfil);
        txtStatus = header.findViewById(R.id.statusProfil);
        imgProfil = header.findViewById(R.id.fotoProfil);
        Header = header.findViewById(R.id.header);

        Header.setOnClickListener(this);

        HashMap<String, String> user = db.getUserDetails();

        name = user.get(User.KEY_NAME);
        email = user.get(User.KEY_EMAIL);

        txtName.setText(name);
        txtEmail.setText(email);
        txtStatus.setText("User");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        MenuTouch();
        PesanTouch();
        recyclerview2.setVisibility(View.GONE);

        getSupportActionBar().setSubtitle("Pilih Mata Pelajaran");

        retrieveImage();
    }

    @Override
    public void onClick(View view) {
        if (view == btnSearch){
            if (gpsTracker.getIsGPSTrackingEnabled()){
                Intent i = new Intent(getApplicationContext(), MapsSearch.class);
                startActivity(i);
            } else {
                gpsTracker.showSettingsAlert();
                gpsTracker.isGPSTrackingEnabled = true;
            }

        }
        if (view == Header){
            Intent i = new Intent(getApplicationContext(), ProfilUser.class);
            startActivity(i);
        }
    }

    public void retrieveImage(){
        try {
            storageReference.child("user/"+uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        HashMap<String, String> user = db.getUserDetails();

        name = user.get(User.KEY_NAME);
        email = user.get(User.KEY_EMAIL);

        txtName.setText(name);
        txtEmail.setText(email);
        txtStatus.setText("User");
    }

    private void Menu(){
        adapter1 = new MenuUserAdapter(menuList);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(this,2);
        recyclerview1.setLayoutManager(mLayoutManager);
        recyclerview1.setItemAnimator(new DefaultItemAnimator());
        menuList();
        recyclerview1.setAdapter(adapter1);
    }

    private void MenuTouch(){
        adapter1 = new MenuUserAdapter(menuList);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(this,2);
        recyclerview1.setLayoutManager(mLayoutManager);
        recyclerview1.setItemAnimator(new DefaultItemAnimator());
        menuList();
        recyclerview1.setAdapter(adapter1);

        recyclerview1.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview1, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position==0){
                    Intent i = new Intent(UserActivity.this, ListPengajar.class);
                    i.putExtra(Pengajar.KEY_PELAJARAN, "Bahasa Indonesia");
                    startActivity(i);
                } else if (position==1){
                    Intent i = new Intent(UserActivity.this, ListPengajar.class);
                    i.putExtra(Pengajar.KEY_PELAJARAN, "Matematika");
                    startActivity(i);
                } else if (position==2){
                    Intent i = new Intent(UserActivity.this, ListPengajar.class);
                    i.putExtra(Pengajar.KEY_PELAJARAN, "IPA");
                    startActivity(i);
                } else if (position==3){
                    Intent i = new Intent(UserActivity.this, ListPengajar.class);
                    i.putExtra(Pengajar.KEY_PELAJARAN, "Bahasa Inggris");
                    startActivity(i);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void menuList(){
        menuList.clear();
        mFitur menu = new mFitur(R.drawable.indo, "Bahasa Indonesia");
        menuList.add(menu);
        menu = new mFitur(R.drawable.matematika, "Matematika");
        menuList.add(menu);
        menu = new mFitur(R.drawable.atom, "Pengetahuan Alam");
        menuList.add(menu);
        menu = new mFitur(R.drawable.inggris, "Bahasa Inggris");
        menuList.add(menu);
        adapter1.notifyDataSetChanged();

    }

    private void Pesan(){
        adapter2 = new ChatUAdapter(this, pesanList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview2.setHasFixedSize(true);
        recyclerview2.setLayoutManager(mLayoutManager);
        recyclerview2.setItemAnimator(new DefaultItemAnimator());
        recyclerview2.setAdapter(adapter2);

        root.orderByChild("id_user").equalTo(firebaseLogin.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pesanList.clear();
                for (DataSnapshot chat : dataSnapshot.getChildren()){
                    mChat lP = chat.getValue(mChat.class);
                    pesanList.add(lP);
                    recyclerview2.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void PesanTouch(){
        recyclerview2.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview2, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(), DirectChatU.class);
                mChat lP = pesanList.get(position);
                i.putExtra("id_user", lP.getId_user());
                i.putExtra("id_pengajar", lP.getId_pengajar());
                i.putExtra("nama_user", lP.getNama_user());
                i.putExtra("nama_pengajar", lP.getNama_pengajar());

                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        firebaseAuth.signOut();

        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
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

        if (id == R.id.listorder) {
            databaseReference.orderByChild("id_user").equalTo(firebaseLogin.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot transaksi : dataSnapshot.getChildren()){
                                mTransaksi lP = transaksi.getValue(mTransaksi.class);
                                if (lP.getId_user().equals(firebaseLogin.getUid())){
                                    Intent i = new Intent(UserActivity.this, ListOrder.class);
                                    i.putExtra(User.KEY_EMAIL, email);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(UserActivity.this, "Anda belum order", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else if (id == R.id.history) {
            Intent i = new Intent(UserActivity.this, HistoryUser.class);
            i.putExtra(User.KEY_EMAIL, email);
            startActivity(i);
        } else if (id == R.id.pesan) {
            Intent i = new Intent(UserActivity.this, PilihPesanU.class);
            i.putExtra("nama_user", name);
            startActivity(i);
        } else if (id == R.id.editprofil) {
            Intent i = new Intent(getApplicationContext(), ProfilUser.class);
            startActivity(i);
        } else if (id == R.id.editpassword) {
            Intent i = new Intent(getApplicationContext(), ChangePasswordU.class);
            startActivity(i);
        } else if (id == R.id.logout) {
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
