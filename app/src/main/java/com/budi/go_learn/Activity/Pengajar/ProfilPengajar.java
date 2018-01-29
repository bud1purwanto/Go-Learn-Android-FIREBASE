package com.budi.go_learn.Activity.Pengajar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.budi.go_learn.Controller.SQLiteHandler;
import com.budi.go_learn.Controller.SessionManager;
import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.R;
import com.budi.go_learn.Variable.Pengajar;
import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfilPengajar extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ProfilPengajar.class.getSimpleName();
    private Button btnEditProfile, buttonChoose, buttonCamera;
    private EditText inputFullName, inputPhone, inputEmail, inputAddress, inputDistrict, inputCity, inputDescription;
    private String name, phone, email, address, district, city, pelajaran, gender, description, active, work;
    private android.widget.RadioGroup RadioGroup;
    private RadioButton radioButton;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private SharedPreferences permissionStatus;
    private Spinner pelajaranSpinner;
    private ArrayAdapter<String> adapterPelajaran;
    private ImageView imageView;
    private Bitmap bitmap;
    private Uri filePath, downloadUrl, fileUri;
    private String filePat = null;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseUser firebaseLogin;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_pengajar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseLogin = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("pengajar");
        storageReference = FirebaseStorage.getInstance().getReference();

        setTitle("Edit Profil");

        inputFullName = (EditText) findViewById(R.id.name);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputEmail = (EditText) findViewById(R.id.email);
        inputAddress = (EditText) findViewById(R.id.address);
        inputDistrict = (EditText) findViewById(R.id.district);
        inputCity = (EditText) findViewById(R.id.city);
        pelajaranSpinner = (Spinner) findViewById(R.id.lokasiSpinner);
        inputDescription = (EditText) findViewById(R.id.description);
        RadioGroup = (android.widget.RadioGroup) findViewById(R.id.RadioGroup);
        btnEditProfile = (Button) findViewById(R.id.btnEditProfil);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        imageView = (ImageView) findViewById(R.id.editimageView);

        inputDescription.setSingleLine(false);
        inputDescription.setHorizontalScrollBarEnabled(false);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        name = user.get(Pengajar.KEY_NAME);
        phone = user.get(Pengajar.KEY_PHONE);
        email = user.get(Pengajar.KEY_EMAIL);
        address = user.get(Pengajar.KEY_ADDRESS);
        pelajaran = user.get(Pengajar.KEY_PELAJARAN);
        gender = user.get(Pengajar.KEY_GENDER);
        description = user.get(Pengajar.KEY_KET);

        RadioButton l = (RadioButton) findViewById(R.id.Laki);
        l.setChecked(true);

        inputFullName.setText(name);
        inputPhone.setText(phone);
        inputEmail.setText(email);
        inputAddress.setText(address);
        inputEmail.setEnabled(false);

        btnEditProfile.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
        buttonCamera.setOnClickListener(this);
        spinnerStatus();
    }

    @Override
    public void onClick(View view) {
        if (view == btnEditProfile) {
            name        = inputFullName.getText().toString().trim();
            phone       = inputPhone.getText().toString().trim();
            email       = inputEmail.getText().toString().trim();
            address     = inputAddress.getText().toString().trim();
            district    = inputDistrict.getText().toString().trim();
            city        = inputCity.getText().toString().trim();
            int selectedRadioButtonID = RadioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedRadioButtonID);
            gender      = radioButton.getText().toString();
            description = inputDescription.getText().toString().trim();
            pelajaran = pelajaranSpinner.getSelectedItem().toString();

            if (bitmap != null){
                showDialog();
                changePhoto();
            } else {
                showDialog();
                changeProfile(name, email, phone, gender, address, district, city, pelajaran, description);
            }
        }

        if (view == buttonChoose) {
            showFileChooser();
        }
        if (view == buttonCamera){
            askPermission();
            captureImage();
        }
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
                        inputFullName.setText(data.getName());
                        inputPhone.setText(data.getPhone());
                        inputEmail.setText(data.getEmail());
                        inputAddress.setText(data.getAddress());
                        inputDistrict.setText(data.getDistrict());
                        inputCity.setText(data.getCity());
                        inputDescription.setText(data.getKeterangan());
                        url = data.getUrl();
                        active = data.getActive();
                        work = data.getWork();
                        Glide.with(getApplicationContext()).load(data.getUrl()).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeProfile(String name, String email, String phone, String gender,
                               String address, String district, String city, String pelajaran, String description){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String urlFoto;
        if (bitmap == null){
            urlFoto = url;
        } else {
            urlFoto = downloadUrl.toString();
        }
        mPengajar userInfo = new mPengajar(user.getUid(), name, email, phone, gender, address, district, city, pelajaran,
                description, "pengajar", active, work, urlFoto);
        databaseReference.child(user.getUid()).setValue(userInfo);
        db.updateUser(name, phone, address, gender, email);
        hideDialog();
    }

    private void changePhoto(){
        String uid = firebaseLogin.getUid();
        StorageReference riversRef = storageReference.child("pengajar/"+uid+".jpg");

        riversRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pDialog.dismiss();
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        changeProfile(name, email, phone, gender, address, district, city, pelajaran, description);
                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        pDialog.setMessage(((int) progress) + "% Uploaded..");
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bitmap == null){
            dataLogin();
        }
    }

    private void spinnerStatus(){
        List<String> list1;
        pelajaranSpinner.setPrompt("Pilih Mata Pelajaran");

        list1 = new ArrayList<String>();
        list1.add("Bahasa Indonesia");
        list1.add("Matematika");
        list1.add("IPA");
        list1.add("Bahasa Inggris");

        adapterPelajaran = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list1);
        adapterPelajaran.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pelajaranSpinner.setAdapter(adapterPelajaran);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                filePat = fileUri.getPath();
                imageView.setVisibility(View.VISIBLE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                bitmap = BitmapFactory.decodeFile(filePat, options);
                filePath = getImageUri(getApplicationContext(), bitmap);
                imageView.setImageBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                imageView.setImageBitmap(bitmap);
                Toast.makeText(getApplicationContext(),
                        "Batal", Toast.LENGTH_SHORT)
                        .show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void askPermission(){
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(ProfilPengajar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfilPengajar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilPengajar.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ProfilPengajar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilPengajar.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ProfilPengajar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();


        } else {
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
//        Toast.makeText(getBaseContext(), "", Toast.LENGTH_LONG).show();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "upload");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + "upload directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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
