package com.budi.go_learn.Activity.User;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.budi.go_learn.Adapter.HistoriUAdapter;
import com.budi.go_learn.Models.mHistori;
import com.budi.go_learn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryUser extends AppCompatActivity {

    private List<mHistori> listHistori;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;
    private HistoriUAdapter historiAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);

        databaseReference = FirebaseDatabase.getInstance().getReference("histori");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setTitle("Histori Transaksi");

        recyclerView = (RecyclerView) findViewById(R.id.rvHistoriUser);


        listHistori = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        historiAdapter = new HistoriUAdapter(this, listHistori);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ActionBar toolbar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listHistori.clear();
        DataCalling();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listHistori.clear();
        DataCalling();
    }

    public void DataCalling(){
        pDialog.setMessage("Loading");
        showDialog();
        databaseReference.orderByChild("id_user").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listHistori.clear();
                        for (DataSnapshot histori : dataSnapshot.getChildren()){
                            mHistori lP = histori.getValue(mHistori.class);
                            listHistori.add(lP);
                            recyclerView.setAdapter(historiAdapter);

                        }
                        hideDialog();
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}