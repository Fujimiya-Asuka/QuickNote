package com.asuka.quicknote.domain;

public class ToDo {
    private long id;
    private String title;
    private String time;
    private int notify;

    public ToDo() {
    }

    public ToDo(long id, String title, String time, int notify) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.notify = notify;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int done) {
        notify = done;
    }
}
