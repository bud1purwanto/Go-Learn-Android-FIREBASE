package com.budi.go_learn.Models;

/**
 * Created by root on 12/15/17.
 */

public class mFitur {
    public int gambar;
    public String judul;

    public mFitur(int gambar, String judul) {
        this.gambar = gambar;
        this.judul = judul;
    }

    public void setGambar(int gambar) {
        this.gambar = gambar;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public int getGambar() {
        return gambar;
    }

    public String getJudul() {
        return judul;
    }
}
