package com.asuka.quicknote.domain;

public class ToDo {
    private long id; //待办ID
    private String title;           //待办标题
    private String time;         //提醒时间，默认是插入和修改的时间（即不提醒）
    private int notify;            //提醒的状态 ： -1 不提醒 0 已提醒 1待提醒
    private int state;              //同步状态：-1已删除 0 本地新增 1本地修改 2已同步
    private int modify;         //从服务器上同步得到的时间戳

    public ToDo() {
    }


    public ToDo(long id, String title, String time, int notify) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.notify = notify;
    }

    /**
     * 待办对象
     * @param id
     * @param title
     * @param time
     * @param notify
     * @param state
     * @param modify
     */
    public ToDo(long id, String title, String time, int notify, int state, int modify) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.notify = notify;
        this.state = state;
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

    @Override
    public String toString() {
        return "ToDo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", notify=" + notify +
                ", state=" + state +
                ", modify=" + modify +
                '}';
    }
}
