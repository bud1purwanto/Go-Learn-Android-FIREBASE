package com.budi.go_learn.Models;

/**
 * Created by root on 1/24/18.
 */

public class mChat {
    public String id_chat, id_user, id_pengajar, nama_user, nama_pengajar;

    public mChat() {
    }

    public mChat(String id_chat, String id_user, String id_pengajar, String nama_user, String nama_pengajar) {
        this.id_chat = id_chat;
        this.id_user = id_user;
        this.id_pengajar = id_pengajar;
        this.nama_user = nama_user;
        this.nama_pengajar = nama_pengajar;
    }

    public String getId_chat() {
        return id_chat;
    }

    public void setId_chat(String id_chat) {
        this.id_chat = id_chat;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_pengajar() {
        return id_pengajar;
    }

    public void setId_pengajar(String id_pengajar) {
        this.id_pengajar = id_pengajar;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public String getNama_pengajar() {
        return nama_pengajar;
    }

    public void setNama_pengajar(String nama_pengajar) {
        this.nama_pengajar = nama_pengajar;
    }
}
