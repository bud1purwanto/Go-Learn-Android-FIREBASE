package com.budi.go_learn.Activity.Public;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.budi.go_learn.Features.GMailSender;
import com.budi.go_learn.R;
import com.budi.go_learn.Server.Server;

public class ForgotPassword extends AppCompatActivity {
    private String email;
    private Button btnSend;
    private EditText inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnSend =  findViewById(R.id.btnSend);
        inputEmail =  findViewById(R.id.email);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Isi email anda!", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(email);
                }
            }
        });
    }

    private void sendMessage(final String imel) {
        final ProgressDialog dialog = new ProgressDialog(ForgotPassword.this);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();

        final String user       = "adm1n.golearn@gmail.com";
        final String pass       = "adm1n.golearn123";
        final String subject    = "Go-Learn (Forgot Password)";
        final String link       = Server.URL + "url.php?email="+imel;
        final String text       = "Untuk Mengganti Password Anda, Silahkan Klik Dibawah Ini \n" +
                "\n<a href='"+link+"'><button style='background-color: black; color: white; border-radius: 10px; font-size: 15px; height: 25px'>Klik Disini Untuk Mengganti Password Anda</button></a>";

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(user, pass);
                    sender.sendMail(subject, text, user, imel);
                    dialog.dismiss();
                    Intent intent = new Intent(ForgotPassword.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }
}
