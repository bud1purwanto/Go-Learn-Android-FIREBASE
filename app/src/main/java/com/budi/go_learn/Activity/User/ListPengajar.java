package com.budi.go_learn.Activity.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.budi.go_learn.Adapter.PengajarAdapter;
import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.R;
import com.budi.go_learn.RecyclerView.ClickListener;
import com.budi.go_learn.RecyclerView.RecyclerTouchListener;
import com.budi.go_learn.Variable.Pengajar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListPengajar extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipe;
    private List<mPengajar> listPengajar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;
    private PengajarAdapter pengajarAdapter;
    private ProgressBar progressBar;
    private String Pelajaran;
    private TextView pesan;
    private Bitmap bitmap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    private static final String TAG = ListPengajar.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pengajar);

        databaseReference = FirebaseDatabase.getInstance().getReference("pengajar");

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        pesan = (TextView) findViewById(R.id.pesan);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        listPengajar = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        pengajarAdapter = new PengajarAdapter(this, listPengajar);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        progressBar.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        Pelajaran = i.getStringExtra(Pengajar.KEY_PELAJARAN);
        setTitle(Pelajaran);

        ActionBar toolbar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setSubtitle("Pilih Pengajar");

        swipe.setOnRefreshListener(this);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(), OrderPengajar.class);
                mPengajar lP = listPengajar.get(position);
                i.putExtra(Pengajar.KEY_UID, lP.getUid());
                i.putExtra(Pengajar.KEY_NAME, lP.getName());
                i.putExtra(Pengajar.KEY_EMAIL, lP.getEmail());
                i.putExtra(Pengajar.KEY_PHONE, lP.getPhone());
                i.putExtra(Pengajar.KEY_GENDER, lP.getGender());
                i.putExtra(Pengajar.KEY_ADDRESS, lP.getAddress());
                i.putExtra(Pengajar.KEY_DISTRICT, lP.getDistrict());
                i.putExtra(Pengajar.KEY_CITY, lP.getCity());
                i.putExtra(Pengajar.KEY_PELAJARAN, lP.getPelajaran());
                i.putExtra(Pengajar.KEY_KET, lP.getKeterangan());
                i.putExtra(Pengajar.KEY_ACTIVE, lP.getActive());
                i.putExtra(Pengajar.KEY_WORK, lP.getWork());
                i.putExtra(Pengajar.KEY_URL, lP.getUrl());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
                mPengajar lP = listPengajar.get(position);
                Toast.makeText(getApplicationContext(), lP.getName(), Toast.LENGTH_SHORT).show();
            }
        }));
    }


    @Override
    protected void onStart() {
        super.onStart();
        listPengajar.clear();
        DataCalling();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPengajar.clear();
        DataCalling();
    }

    @Override
    public void onRefresh() {
        listPengajar.clear();
        DataCalling();
        swipe.setRefreshing(false);
    }

    public void DataCalling(){
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.orderByChild("pelajaran").equalTo(Pelajaran)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listPengajar.clear();
                        for (DataSnapshot pengajar : dataSnapshot.getChildren()){
                            mPengajar lP = pengajar.getValue(mPengajar.class);
                            if (lP.getActive().equals("1") && lP.getWork().equals("0")){
                                listPengajar.add(lP);
                                recyclerView.setAdapter(pengajarAdapter);
                            }
                            if (lP.getActive().equals("0") || lP.getWork().equals("1")){
                                listPengajar.remove(lP);
                                recyclerView.setAdapter(pengajarAdapter);
                            }                        }
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void CariData(final String keyword){
        databaseReference.orderByChild("name")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listPengajar.clear();
                for (DataSnapshot pengajar : dataSnapshot.getChildren()){
                    mPengajar lP = pengajar.getValue(mPengajar.class);
                    if ((containsIgnoreCase(lP.getName(), keyword) || containsIgnoreCase(lP.getCity(), keyword))
                            && lP.getActive().equals("1") && lP.getWork().equals("0") && lP.getPelajaran().equals(Pelajaran)){
                        listPengajar.add(lP);
                        recyclerView.setAdapter(pengajarAdapter);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), "Data tidak ditemukan",Toast.LENGTH_LONG).show();
            }
        });
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.action_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.type_name));
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String keyword) {
        CariData(keyword);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String keyword) {
        CariData(keyword);
        return false;
    }
}