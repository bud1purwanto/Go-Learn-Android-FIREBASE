package com.budi.go_learn.Activity.Public;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.budi.go_learn.Activity.Admin.AdminActivity;
import com.budi.go_learn.Activity.Pengajar.PengajarActivity;
import com.budi.go_learn.Activity.User.UserActivity;
import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Controller.SessionManager;
import com.budi.go_learn.Models.mUser;
import com.budi.go_learn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister, btnLoginPengajar, btnForgotPassword;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        setTitle("Login User");
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnLoginPengajar = (Button) findViewById(R.id.btnLoginPengajar);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());


        if (session.isLoggedIn() && firebaseAuth.getCurrentUser() != null) {
            HashMap<String, String> user = db.getUserDetails();

            String status = user.get("status");
            if (status.equalsIgnoreCase("admin")){
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            }else if (status.equalsIgnoreCase("pengajar")){
                Intent intent = new Intent(LoginActivity.this, PengajarActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                boolean cancel = false;
                View focusView = null;

                if (!isPasswordValid(password)) {
                    inputPassword.setError(getString(R.string.error_invalid_password));
                    focusView = inputPassword;
                    cancel = true;
                }

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getString(R.string.error_field_required));
                    focusView = inputEmail;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    inputEmail.setError(getString(R.string.error_invalid_email));
                    focusView = inputEmail;
                    cancel = true;
                }

                else if (cancel) {
                    focusView.requestFocus();
                }

                else if (!email.isEmpty() && !password.isEmpty()) {
                    cekLogin(email, password);
                }
            }

        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLoginPengajar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginPengajar.class);
                startActivity(i);
                finish();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ForgotPassword.class);
                startActivity(i);
            }
        });
    }

    private void cekLogin(String email, String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            session.setLogin(true);
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot user : dataSnapshot.getChildren()){
                                        mUser data = user.getValue(mUser.class);
                                        FirebaseUser u = firebaseAuth.getCurrentUser();
                                        if(u.getEmail().equals(data.getEmail())){
                                            String status = data.getStatus();
                                            if (status.equals("admin")){
                                                db.addUser(data.getName(), data.getEmail(), data.getPhone(),
                                                        data.getGender(), data.getAddress(),u.getUid(), "", status);
                                                Intent intent = new Intent(LoginActivity.this,
                                                        AdminActivity.class);
                                                hideDialog();
                                                startActivity(intent);
                                                finish();
                                            } else if(status.equals("user")){
                                                db.addUser(data.getName(), data.getEmail(), data.getPhone(),
                                                        data.getGender(), data.getAddress(),u.getUid(), "", status);
                                                Intent intent = new Intent(LoginActivity.this,
                                                        UserActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            hideDialog();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Email atau password salah", Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });

    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
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