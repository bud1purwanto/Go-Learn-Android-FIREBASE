package com.budi.go_learn.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budi.go_learn.Models.mChat;
import com.budi.go_learn.R;

import java.util.List;

/**
 * Created by root on 1/24/18.
 */

public class ChatPAdapter extends RecyclerView.Adapter<ChatPAdapter.ViewHolder> {

    Context context;
    List<mChat> listChat;

    public ChatPAdapter(Context context, List<mChat> listChat) {
        this.context = context;
        this.listChat = listChat;
    }

    @Override
    public ChatPAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ChatPAdapter.ViewHolder viewHolder = new ChatPAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatPAdapter.ViewHolder holder, final int position) {
        mChat data =  listChat.get(position);
        holder.namaChat.setText(data.getNama_user());
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView namaChat;
        public ViewHolder(View itemView) {
            super(itemView);
            namaChat = (TextView) itemView.findViewById(R.id.namaChat);
        }
    }
}