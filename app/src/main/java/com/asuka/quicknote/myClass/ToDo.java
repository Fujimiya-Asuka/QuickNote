package com.asuka.quicknote.myClass;

public class ToDo {
    private long id;
    private String title;
    private String time;
    private int isDone;

    public ToDo() {
    }

    public ToDo(long id, String title, String time, int isDone) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.isDone = isDone;
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

    public int isDone() {
        return isDone;
    }

    public void setDone(int done) {
        isDone = done;
    }
}
