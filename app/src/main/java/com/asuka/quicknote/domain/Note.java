package com.asuka.quicknote.domain;

public class Note {
    private long id;//主键
    private String title;//标题
    private String data;//内容
    private String image;//图片名
    private String time;//时间
    private int state;//状态
    private int modify;//时间戳

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

    public Note(long id, String title, String data, String image, String time, int state, int modify) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.image = image;
        this.time = time;
        this.state = state;
        this.modify = modify;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getModify() {
        return modify;
    }

    public void setModify(int modify) {
        this.modify = modify;
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

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", image='" + image + '\'' +
                ", time='" + time + '\'' +
                ", state=" + state +
                ", modify=" + modify +
                '}';
    }

}
