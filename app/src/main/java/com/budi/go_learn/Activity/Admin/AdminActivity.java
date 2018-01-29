package com.budi.go_learn.Activity.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.budi.go_learn.Activity.Public.LoginActivity;
import com.budi.go_learn.Adapter.AdminAdapter1;
import com.budi.go_learn.Adapter.AdminAdapter2;
import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Controller.SessionManager;
import com.budi.go_learn.Models.mFitur;
import com.budi.go_learn.R;
import com.budi.go_learn.RecyclerView.ClickListener;
import com.budi.go_learn.RecyclerView.RecyclerTouchListener;
import com.budi.go_learn.Variable.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtStatus;
    private ConstraintLayout imgProfil;
    private SQLiteHandler db;
    private SessionManager session;
    private RecyclerView recyclerview;
    private AdminAdapter1 adapter1;
    private AdminAdapter2 adapter2;
    private List<mFitur> menuList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Halaman Admin");

        firebaseAuth = FirebaseAuth.getInstance();

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn() && firebaseAuth.getCurrentUser() == null) {
            logoutUser();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        txtName = header.findViewById(R.id.namaProfil);
        txtEmail = header.findViewById(R.id.emailProfil);
        txtStatus = header.findViewById(R.id.statusProfil);
        imgProfil = header.findViewById(R.id.header);

        recyclerview = (RecyclerView) findViewById(R.id.rvHome);

        Menu();

        HashMap<String, String> user = db.getUserDetails();

        String name = user.get(User.KEY_NAME);
        String email = user.get(User.KEY_EMAIL);
        String status = user.get(User.KEY_STATUS);

        txtName.setText(name);
        txtEmail.setText(email);
        txtStatus.setText("Admin");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    private void Menu(){
        adapter1 = new AdminAdapter1(menuList);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(this,2);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        menuList();
        recyclerview.setAdapter(adapter1);
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent fitur1 = new Intent(getApplicationContext(), AddPengajar.class);
                Intent fitur2 = new Intent(getApplicationContext(), ListUser.class);
                Intent fitur3 = new Intent(getApplicationContext(), ListPengajar2.class);
                if(position==0){
                    startActivity(fitur1);
                } else if (position==1){
                    startActivity(fitur2);
                } else if (position==2){
                    startActivity(fitur3);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void menuList(){
        menuList.clear();
        mFitur menu = new mFitur(R.drawable.dash1, "Add Pengajar");
        menuList.add(menu);
        menu = new mFitur(R.drawable.dash2, "List User");
        menuList.add(menu);
        menu = new mFitur(R.drawable.dash3, "List Pengajar");
        menuList.add(menu);
        menu = new mFitur(R.drawable.dash1, "Histori");
        menuList.add(menu);
        adapter1.notifyDataSetChanged();

    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        firebaseAuth.signOut();
        finish();
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
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

        if (id == R.id.nav_addpengajar) {
            Intent i = new Intent(getApplicationContext(), AddPengajar.class);
            startActivity(i);
        } else if (id == R.id.nav_listuser) {
            Intent i = new Intent(getApplicationContext(), ListUser.class);
            startActivity(i);
        } else if (id == R.id.nav_listpengajar) {

        } else if (id == R.id.nav_listtransaksi) {

        } else if (id == R.id.nav_listhistori) {

        } else if (id == R.id.editprofil) {

        } else if (id == R.id.nav_logout) {
            KonfirmasiLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
