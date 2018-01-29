package com.budi.go_learn.Activity.Pengajar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.budi.go_learn.Adapter.ChatPAdapter;
import com.budi.go_learn.Models.mChat;
import com.budi.go_learn.R;
import com.budi.go_learn.RecyclerView.ClickListener;
import com.budi.go_learn.RecyclerView.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PilihPesanP extends AppCompatActivity {

    private DatabaseReference root;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private List<mChat> listChat;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;
    private ChatPAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_pesan_p);
        setTitle("Daftar Pesan");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference().child("chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.rvChat);

        listChat = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        chatAdapter = new ChatPAdapter(this, listChat);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        root.orderByChild("id_pengajar").equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listChat.clear();
                for (DataSnapshot chat : dataSnapshot.getChildren()){
                    mChat lP = chat.getValue(mChat.class);
                    listChat.add(lP);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(), DirectChatP.class);
                mChat lP = listChat.get(position);
                i.putExtra("id_user", lP.getId_user());
                i.putExtra("id_pengajar", lP.getId_pengajar());
                i.putExtra("nama_user", lP.getNama_user());
                i.putExtra("nama_pengajar", lP.getNama_pengajar());

                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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