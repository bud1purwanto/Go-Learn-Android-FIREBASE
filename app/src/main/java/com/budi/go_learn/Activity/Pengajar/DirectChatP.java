package com.budi.go_learn.Activity.Pengajar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.budi.go_learn.Adapter.IsiChatPAdapter;
import com.budi.go_learn.Models.mIsiChat;
import com.budi.go_learn.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DirectChatP extends AppCompatActivity {
    private Button btn_send_msg;
    private EditText input_msg;
    private String room_name, id_user, id_pengajar;
    public static String namaPengajar, namaUser;
    private DatabaseReference root;
    private String temp_key;
    private List<mIsiChat> listChat;
    private RecyclerView recyclerView;
    private LinearLayoutManager recyclerViewlayoutManager;
    private IsiChatPAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_chat_p);
        btn_send_msg = (Button)findViewById(R.id.button);
        input_msg = (EditText)findViewById(R.id.editText);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.rvIsiChat);

        listChat = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        chatAdapter = new IsiChatPAdapter(this, listChat);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerViewlayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {

                recyclerView.scrollToPosition(listChat.size());

            }
        });


        id_user = getIntent().getExtras().get("id_user").toString();
        id_pengajar = getIntent().getExtras().get("id_pengajar").toString();
        namaPengajar = getIntent().getExtras().get("nama_pengajar").toString();
        namaUser = getIntent().getExtras().get("nama_user").toString();
        room_name = id_user+" - "+id_pengajar;
        setTitle(namaUser);

        root = FirebaseDatabase.getInstance().getReference().child("chat").child(room_name);

        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put("id", room_name);
        map2.put("id_user", id_user);
        map2.put("id_pengajar", id_pengajar);
        map2.put("nama_user", namaUser);
        map2.put("nama_pengajar", namaPengajar);

        root.updateChildren(map2);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(input_msg.getText().toString())) {
                    Map<String,Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date();

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String,Object> map2 = new HashMap<String, Object>();
                    map2.put("name",namaPengajar);
                    map2.put("msg",input_msg.getText().toString());
                    map2.put("time", dateFormat.format(date));

                    message_root.updateChildren(map2);
                    input_msg.setText("");
                }

            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversatin(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversatin(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void append_chat_conversatin(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext())
        {
            mIsiChat lP = new mIsiChat();
            lP.setName((String) ((DataSnapshot)i.next()).getValue());
            lP.setMsg((String) ((DataSnapshot)i.next()).getValue());
            lP.setTime((String) ((DataSnapshot)i.next()).getValue());
            listChat.add(lP);
            recyclerView.setAdapter(chatAdapter);
        }
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