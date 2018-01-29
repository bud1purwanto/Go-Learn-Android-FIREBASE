package com.budi.go_learn.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.budi.go_learn.Models.mFitur;
import com.budi.go_learn.R;

import java.util.List;

/**
 * Created by root on 12/11/17.
 */

public class AdminAdapter1 extends RecyclerView.Adapter<AdminAdapter1.MyViewHolder> {
    List<mFitur> listMFiturs;

    public AdminAdapter1(List<mFitur> listMenu) {
        this.listMFiturs = listMenu;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu,parent,false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mFitur list = this.listMFiturs.get(position);
        holder.txtJudul.setText(list.getJudul());
        holder.imgGambar.setImageResource(list.getGambar());
    }

    @Override
    public int getItemCount() {
        return listMFiturs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtJudul;
        public ImageView imgGambar;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtJudul = itemView.findViewById(R.id.txtJudul);
            imgGambar = itemView.findViewById(R.id.imgGambar);

        }
    }
}
