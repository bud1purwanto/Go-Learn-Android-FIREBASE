package com.budi.go_learn.Models;

/**
 * Created by root on 1/24/18.
 */

public class mIsiChat {
    String name, msg, time;

    public mIsiChat() {
    }

    public mIsiChat(String name, String msg, String time) {
        this.name = name;
        this.msg = msg;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
