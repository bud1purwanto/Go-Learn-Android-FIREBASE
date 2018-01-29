package com.budi.go_learn.Models;

/**
 * Created by root on 1/17/18.
 */

public class mUser {
    public String uid,  name, email, phone, gender, address, pelajaran, status, active, work, url;

    public mUser() {
    }

    public mUser(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public mUser(String name, String email, String phone, String gender, String address, String status, String url) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.status = status;
        this.url = url;
    }

    public mUser(String uid, String email, String status) {
        this.uid = uid;
        this.email = email;
        this.status = status;
    }

    public mUser(String name, String email, String phone, String gender, String address, String pelajaran, String status, String active, String work) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.pelajaran = pelajaran;
        this.status = status;
        this.active = active;
        this.work = work;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getPelajaran() {
        return pelajaran;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getActive() {
        return active;
    }

    public String getWork() {
        return work;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPelajaran(String pelajaran) {
        this.pelajaran = pelajaran;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
