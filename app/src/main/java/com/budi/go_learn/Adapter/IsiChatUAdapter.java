package com.budi.go_learn.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.budi.go_learn.Activity.User.DirectChatU;
import com.budi.go_learn.Models.mIsiChat;
import com.budi.go_learn.R;

import java.util.List;

/**
 * Created by root on 1/24/18.
 */

public class IsiChatUAdapter extends RecyclerView.Adapter<IsiChatUAdapter.ViewHolder> {

    Context context;
    List<mIsiChat> listChat;

    public IsiChatUAdapter(Context context, List<mIsiChat> listChat) {
        this.context = context;
        this.listChat = listChat;
    }

    @Override
    public IsiChatUAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_isi_chat, parent, false);
        IsiChatUAdapter.ViewHolder viewHolder = new IsiChatUAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IsiChatUAdapter.ViewHolder holder, final int position) {
        mIsiChat data =  listChat.get(position);
        if (data.getMsg().equals(DirectChatU.namaUser)){
            holder.btnSender.setText(data.getName());
            holder.timeSender.setText(data.getTime());
            holder.btnReceiver.setVisibility(View.GONE);
            holder.timeReceiver.setVisibility(View.GONE);
        } else {
            holder.btnReceiver.setText(data.getName());
            holder.timeReceiver.setText(data.getTime());
            holder.btnSender.setVisibility(View.GONE);
            holder.timeSender.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public Button btnReceiver, btnSender;
        public TextView timeReceiver, timeSender;
        public ViewHolder(View itemView) {
            super(itemView);
            btnReceiver = (Button) itemView.findViewById(R.id.btnReceiver);
            btnSender = (Button) itemView.findViewById(R.id.btnSender);
            timeReceiver = (TextView) itemView.findViewById(R.id.timeReceiver);
            timeSender = (TextView) itemView.findViewById(R.id.timeSender);
        }
    }
}