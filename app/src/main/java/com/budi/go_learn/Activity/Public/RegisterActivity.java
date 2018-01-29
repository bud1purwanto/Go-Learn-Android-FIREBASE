package com.budi.go_learn.Activity.Public;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.budi.go_learn.Activity.Admin.AdminActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends Activity implements View.OnClickListener{
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister, btnLinkToLogin;
    private EditText inputFullname, inputPhone, inputEmail, inputPassword, inputAddress;
    private RadioGroup RadioGroup;
    private RadioButton radioButton;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        inputFullname = (EditText) findViewById(R.id.name);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputAddress = (EditText) findViewById(R.id.address);
        RadioGroup = (android.widget.RadioGroup) findViewById(R.id.RadioGroup);

        RadioButton l = (RadioButton) findViewById(R.id.Laki);
        l.setChecked(true);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            HashMap<String, String> user = db.getUserDetails();

            String status = user.get("status");
            if (status.equalsIgnoreCase("admin")){
                Intent intent = new Intent(RegisterActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullname.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String address = inputAddress.getText().toString().trim();
                int selectedRadioButtonID = RadioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedRadioButtonID);
                String gender = radioButton.getText().toString().trim();

                if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                        !address.isEmpty()) {
                    registerUser(name, phone, email, password, address, gender);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Masukkan semua data!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void registerUser(final String name, final String phone, final String email, final String password,
                              final String address, final String gender){
        pDialog.setMessage("Registering ...");
        showDialog();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            mUser userInfo = new mUser();
                            userInfo.setUid(user.getUid());
                            userInfo.setName(name);
                            userInfo.setPhone(phone);
                            userInfo.setEmail(email);
                            userInfo.setAddress(address);
                            userInfo.setStatus("user");
                            userInfo.setGender(gender);
                            databaseReference.child(user.getUid()).setValue(userInfo);

                            Toast.makeText(getApplicationContext(), "Sukses", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            hideDialog();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

    }
}
