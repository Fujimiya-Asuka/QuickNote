package com.asuka.quicknote.domain;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
    private String time;

    public Time(Date date) {
        this.time = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date);
    }

    public String getTime() {
        return time;
    }
}
