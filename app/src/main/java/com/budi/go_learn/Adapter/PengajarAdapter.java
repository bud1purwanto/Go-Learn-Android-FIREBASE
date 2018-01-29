package com.budi.go_learn.Adapter;

/**
 * Created by root on 12/28/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.budi.go_learn.Models.mPengajar;
import com.budi.go_learn.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PengajarAdapter extends RecyclerView.Adapter<PengajarAdapter.ViewHolder> {

    Context context;
    List<mPengajar> listPengajar;

    public PengajarAdapter(Context context, List<mPengajar> listPengajar) {
        this.context = context;
        this.listPengajar = listPengajar;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengajar, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mPengajar data =  listPengajar.get(position);
        holder.nameP.setText(data.getName());
        holder.genderP.setText(data.getGender());
        holder.cityP.setText(data.getCity());
        if (data.getUrl() != null){
            Glide.with(context).load(data.getUrl()).apply(RequestOptions.circleCropTransform()).into(holder.imageP);
        }

    }

    @Override
    public int getItemCount() {
        return listPengajar.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameP, genderP, cityP;
        public ImageView imageP;
        public ViewHolder(View itemView) {
            super(itemView);
            nameP = (TextView) itemView.findViewById(R.id.nameP);
            genderP = (TextView) itemView.findViewById(R.id.genderP);
            cityP = (TextView) itemView.findViewById(R.id.cityP);
            imageP = (ImageView) itemView.findViewById(R.id.imageP);
        }
    }
}