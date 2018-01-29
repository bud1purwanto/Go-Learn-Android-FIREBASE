package com.budi.go_learn.Activity.Admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.budi.go_learn.Adapter.ListUserAdapter;
import com.budi.go_learn.Models.mUser;
import com.budi.go_learn.R;
import com.budi.go_learn.RecyclerView.ClickListener;
import com.budi.go_learn.RecyclerView.RecyclerTouchListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListUser extends AppCompatActivity {
    private static final String TAG = ListUser.class.getSimpleName();
    private List<mUser> listUser;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;
    private ListUserAdapter userAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        recyclerView = (RecyclerView) findViewById(R.id.rvListUser);

        Toast.makeText(getApplicationContext(), "Tekan untuk menghapus", Toast.LENGTH_SHORT).show();

        listUser = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        userAdapter = new ListUserAdapter(this, listUser);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        ActionBar toolbar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("List User");

        DataCalling();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                mUser lP = listUser.get(position);
                KonfirmasiHapus(lP);
            }

            @Override
            public void onLongClick(View view, int position) {
                mUser lP = listUser.get(position);
                KonfirmasiHapus(lP);
            }
        }));
    }

    private void KonfirmasiHapus(final mUser lP){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Kamu Yakin Ingin Hapus User?");

        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mUser user = new mUser();
                        databaseReference.child(lP.getUid()).setValue(user);
                        Toast.makeText(getApplicationContext(), "Terhapus", Toast.LENGTH_SHORT).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void DataCalling(){
        databaseReference.orderByChild("status").equalTo("user").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listUser.clear();
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            mUser lP = user.getValue(mUser.class);
                            listUser.add(lP);
                            recyclerView.setAdapter(userAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
