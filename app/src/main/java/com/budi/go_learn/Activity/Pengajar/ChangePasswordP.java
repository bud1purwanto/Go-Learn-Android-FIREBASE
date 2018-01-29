package com.budi.go_learn.Activity.Pengajar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.budi.go_learn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordP extends AppCompatActivity {
    private static final String TAG = ChangePasswordP.class.getSimpleName();
    private EditText passwordlama, passwordbaru, konfirmasipasswordbaru;
    private Button btnChangePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseLogin;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_p);

        setTitle("Ganti Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseLogin = firebaseAuth.getCurrentUser();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        passwordlama = findViewById(R.id.passwordlama);
        passwordbaru = findViewById(R.id.passwordbaru);
        konfirmasipasswordbaru = findViewById(R.id.konfirmasipasswordbaru);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldpas = passwordlama.getText().toString().trim();
                String newpas = passwordbaru.getText().toString().trim();
                String conpas = konfirmasipasswordbaru.getText().toString().trim();

                if(TextUtils.isEmpty(oldpas) || TextUtils.isEmpty(newpas) || TextUtils.isEmpty(conpas)){
                    Toast.makeText(getApplicationContext(),"Isi semua data", Toast.LENGTH_SHORT).show();
                } else if(!newpas.equals(conpas)){
                    Toast.makeText(getApplicationContext(),"Password tidak sama dengan konfirmasi password", Toast.LENGTH_SHORT).show();
                } else {
                    changePassword(oldpas, newpas);
                }
            }
        });
    }

    private void changePassword(String oldpas, final String newpas){
        showDialog();
        pDialog.setMessage("Loading");
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseLogin.getEmail(), oldpas);

        firebaseLogin.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseLogin.updatePassword(newpas).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),
                                                "Password Updated", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Password updated");
                                        hideDialog();
                                        startActivity(new Intent(getApplicationContext(), PengajarActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Password not Updated", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Error password not updated");
                                        hideDialog();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Password lama salah", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error auth failed");
                            hideDialog();
                        }
                    }
                });
    }

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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
