package com.budi.go_learn.Activity.Public;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budi.go_learn.Controller.RequestHandler;
import com.budi.go_learn.R;
import com.budi.go_learn.Server.Konfigurasi;
import com.budi.go_learn.Variable.User;

import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {
    private static final String TAG = ChangePassword.class.getSimpleName();
    private static final String LAUNCH_FROM_URL = "com.androidsrc.launchfrombrowser";
    private TextView email;
    private EditText inputPassword, inputPasswordConfirmation;
    private Button btnSend;
    private String msgFromBrowserUrl, password, passwordconfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        email = findViewById(R.id.email);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordConfirmation = findViewById(R.id.inputKonfirmasiPassword);
        btnSend = findViewById(R.id.btnSend);

        Intent intent = getIntent();
        if(intent != null && intent.getAction().equals(LAUNCH_FROM_URL)){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                msgFromBrowserUrl = bundle.getString("msg_from_browser");
                email.setText(msgFromBrowserUrl);
            }
        } else{
            email.setText("Normal application launch");
        }
         btnSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 password = inputPassword.getText().toString();
                 passwordconfirmation= inputPasswordConfirmation.getText().toString();
                 if (!password.equals(passwordconfirmation)){
                     Toast.makeText(getApplicationContext(), "Password tidak sama dengan konfirmasi",Toast.LENGTH_SHORT).show();
                 } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordconfirmation)){
                     Toast.makeText(getApplicationContext(), "Isi semua data",Toast.LENGTH_SHORT).show();
                 } else {
                     resetPassword(msgFromBrowserUrl, password);
                 }
             }
         });
    }

    private void resetPassword(final String email, final String password){
       

    }

    private void changePassword(final String email, final String password){
        class changePassword extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ChangePassword.this,"Changing Password","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ChangePassword.this, s, Toast.LENGTH_LONG).show();
                if (s.equals("Berhasil Ganti Password")){
                    Intent intent = new Intent(ChangePassword.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(User.KEY_EMAIL, email);
                data.put(User.KEY_PASSWORD, password);

                String result = rh.sendPostRequest(Konfigurasi.URL_CHANGEPASSWORD_USER , data);
                return result;
            }
        }

        changePassword ae = new changePassword();
        ae.execute();
    }
}