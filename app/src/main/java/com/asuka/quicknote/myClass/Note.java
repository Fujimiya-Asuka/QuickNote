package com.asuka.quicknote.myClass;

public class Note {
    private long id;//主键
    private String title;//标题
    private String data;//内容
    private String time;//时间

    public Note() {
    }

    public Note(long id) {
        this.id = id;
    }

    public Note(String title, String data,long id) {
        this.title = title;
        this.data = data;
        this.id = id;
    }

    public Note(String title, String data,long id,String time) {
        this.title = title;
        this.data = data;
        this.id = id;
        this.time = time;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
