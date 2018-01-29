package com.budi.go_learn.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budi.go_learn.Models.mHistori;
import com.budi.go_learn.R;

import java.util.List;

/**
 * Created by root on 1/24/18.
 */

public class HistoriPAdapter extends RecyclerView.Adapter<HistoriPAdapter.ViewHolder> {

    Context context;
    List<mHistori> listHistori;

    public HistoriPAdapter(Context context, List<mHistori> listHistori) {
        this.context = context;
        this.listHistori = listHistori;
    }

    @Override
    public HistoriPAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_histori, parent, false);
        HistoriPAdapter.ViewHolder viewHolder = new HistoriPAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoriPAdapter.ViewHolder holder, final int position) {
        mHistori data =  listHistori.get(position);
        holder.namaPengajar.setText(data.getNameU());
        holder.pelajaranPengajar.setText(data.getEmailU());
        holder.tanggalMulai.setText(data.getCreated_at());
        holder.tanggalSelesai.setText(data.getFinished_at());
    }

    @Override
    public int getItemCount() {
        return listHistori.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView namaPengajar, pelajaranPengajar, tanggalSelesai, tanggalMulai;
        public ViewHolder(View itemView) {
            super(itemView);
            namaPengajar = (TextView) itemView.findViewById(R.id.namaPengajar);
            pelajaranPengajar = (TextView) itemView.findViewById(R.id.pelajaranPengajar);
            tanggalMulai = (TextView) itemView.findViewById(R.id.tanggalMulai);
            tanggalSelesai = (TextView) itemView.findViewById(R.id.tanggalSelesai);
        }
    }
}