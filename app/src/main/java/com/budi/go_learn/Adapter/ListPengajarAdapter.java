package com.budi.go_learn.Adapter;

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

/**
 * Created by root on 1/29/18.
 */

public class ListPengajarAdapter extends RecyclerView.Adapter<ListPengajarAdapter.ViewHolder> {

    Context context;
    List<mPengajar> listPengajar;

    public ListPengajarAdapter(Context context, List<mPengajar> listPengajar) {
        this.context = context;
        this.listPengajar = listPengajar;
    }

    @Override
    public ListPengajarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengajar, parent, false);
        ListPengajarAdapter.ViewHolder viewHolder = new ListPengajarAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListPengajarAdapter.ViewHolder holder, final int position) {
        mPengajar data =  listPengajar.get(position);
        holder.nameP.setText(data.getName());
        holder.genderP.setText(data.getGender());
        if (data.getActive().equals("1")){
            holder.cityP.setText("Aktif");
        } else {
            holder.cityP.setText("Nonaktif");
        }
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