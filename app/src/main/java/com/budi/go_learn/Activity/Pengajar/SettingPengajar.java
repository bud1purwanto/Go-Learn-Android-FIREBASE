package com.budi.go_learn.Activity.Pengajar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingPengajar extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener{

    private TextView txtAktif, txtKeterangan;
    private Switch switchAktif;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseLogin;
    private String uid, name, email, phone, gender, address, district, city, pelajaran, keterangan, status, active, work, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pengajar);

        setTitle("Setting Pengajar");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseLogin = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("pengajar");

        txtAktif = findViewById(R.id.txtAktif);
        txtKeterangan = findViewById(R.id.txtKeterangan);
        switchAktif = findViewById(R.id.switchAktif);
        switchAktif.setOnCheckedChangeListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dataLogin();
    }

    public void dataLogin(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    mPengajar data = user.getValue(mPengajar.class);
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    if(u.getEmail().equals(data.getEmail())){
                        uid = data.getUid();
                        name = data.getName();
                        email = data.getEmail();
                        phone = data.getPhone();
                        gender = data.getGender();
                        address = data.getAddress();
                        district = data.getDistrict();
                        city = data.getCity();
                        pelajaran = data.getPelajaran();
                        keterangan = data.getKeterangan();
                        status = data.getStatus();
                        active = data.getActive();
                        work = data.getWork();
                        url = data.getUrl();

                        if (data.getActive().equals("1")){

                            switchAktif.setChecked(true);
                            txtAktif.setText("Anda Aktif Mengajar");
                            txtKeterangan.setText("Tekan untuk menonaktifkan akun anda");
                        } else if (data.getActive().equals("0")){

                            switchAktif.setChecked(false);
                            txtAktif.setText("Anda Tidak Aktif Mengajar");
                            txtKeterangan.setText("Tekan untuk mengaktifkan akun anda");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked) {
            Aktif();
        } else {
            TidakAktif();
        }
    }

    public void Aktif(){
        mPengajar pengajar = new mPengajar(uid, name, email, phone, gender, address, district, city, pelajaran, keterangan, status, "1", "0", url);
        databaseReference.child(firebaseLogin.getUid()).setValue(pengajar);
        Toast.makeText(getApplicationContext(), "Akun anda diaktifkan", Toast.LENGTH_SHORT).show();
    }

    public void TidakAktif(){
        mPengajar pengajar = new mPengajar(uid, name, email, phone, gender, address, district, city, pelajaran, keterangan, status, "0", "0", url);
        databaseReference.child(firebaseLogin.getUid()).setValue(pengajar);
        Toast.makeText(getApplicationContext(), "Akun anda dinonaktifkan", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
