package com.budi.go_learn.Activity.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Controller.SessionManager;
import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPengajar extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = AddPengajar.class.getSimpleName();
    private Button btnRegister;
    private EditText inputFullname, inputPhone, inputEmail, inputPassword, inputAddress, inputDistrict, inputCity, inputDescription;
    private RadioGroup RadioGroup;
    private RadioButton radioButton;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private FirebaseAuth firebaseAuth, pengAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pengajar);
        setTitle("Tambah Pengajar");

        firebaseAuth = FirebaseAuth.getInstance();
        pengAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("pengajar");

        inputFullname = (EditText) findViewById(R.id.name);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputAddress = (EditText) findViewById(R.id.address);
        inputDistrict = (EditText) findViewById(R.id.district);
        inputCity = (EditText) findViewById(R.id.city);
        inputDescription = (EditText) findViewById(R.id.description);
        RadioGroup = (android.widget.RadioGroup) findViewById(R.id.RadioGroup);

        RadioButton l = (RadioButton) findViewById(R.id.Laki);
        l.setChecked(true);

        btnRegister = (Button) findViewById(R.id.btnEditProfil);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btnRegister){
            String name = inputFullname.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String district = inputDistrict.getText().toString().trim();
            String city = inputCity.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            int selectedRadioButtonID = RadioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedRadioButtonID);
            String gender = radioButton.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                    !address.isEmpty() && !district.isEmpty() && !city.isEmpty() && !description.isEmpty()) {
                addPengajar(name, phone, email, password, address, district, city, description, gender);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Masukkan semua data!", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void addPengajar(final String name, final String phone, final String email, final String password,
                             final String address, final String district, final String city,
                             final String description, final String gender){
        pDialog.setMessage("Registering ...");
        showDialog();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = pengAuth.getCurrentUser();
                            mPengajar userInfo = new mPengajar();
                            userInfo.setName(name);
                            userInfo.setPhone(phone);
                            userInfo.setEmail(email);
                            userInfo.setAddress(address);
                            userInfo.setDistrict(district);
                            userInfo.setCity(city);
                            userInfo.setKeterangan(description);
                            userInfo.setGender(gender);
                            userInfo.setActive("1");
                            userInfo.setWork("0");
                            userInfo.setStatus("pengajar");

                            databaseReference.child(user.getUid()).setValue(userInfo);
                            Toast.makeText(getApplicationContext(), "Berhasil Menambah Pengajar", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPengajar.this, AdminActivity.class);
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


}
