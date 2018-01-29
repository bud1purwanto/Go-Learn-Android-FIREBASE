package com.budi.go_learn.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budi.go_learn.R;

/**
 * Created by root on 12/11/17.
 */

public class AdminAdapter2 extends RecyclerView.Adapter<AdminAdapter2.MyViewHolder> {

    @Override
    public AdminAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pendidikan,parent,false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdminAdapter2.MyViewHolder holder, int position) {
        holder.namaSekolah.setText("Polinema");
        holder.tingkatPendidikan.setText("D4");
        holder.tahunLulus.setText("2018");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView namaSekolah, tahunLulus, tingkatPendidikan;
        public MyViewHolder(View itemView) {
            super(itemView);
            namaSekolah = (TextView) itemView.findViewById(R.id.NamaSekolah);
            tingkatPendidikan = (TextView) itemView.findViewById(R.id.TingkatPendidikan);
            tahunLulus = (TextView) itemView.findViewById(R.id.Tahun);

        }
    }
}
