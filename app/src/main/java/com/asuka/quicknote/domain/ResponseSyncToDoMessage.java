package com.asuka.quicknote.domain;

public class ResponseSyncToDoMessage {
    private String requestUser;
    private int todoID;
    private int resultCode;
    private int modify;



    public ResponseSyncToDoMessage() {
    }

    /**
     *
     * @Author:  XuZhenHui
     * @Time:  2021/4/22 22:39
     * @param requestUser
     * @param todoID
     * @param resultCode
     * @param modify
     */
    public ResponseSyncToDoMessage(String requestUser,int todoID, int resultCode, int modify) {
        this.requestUser = requestUser;
        this.todoID = todoID;
        this.resultCode = resultCode;
        this.modify = modify;
    }

    public int getTodoID() {
        return todoID;
    }

    public void setTodoID(int todoID) {
        this.todoID = todoID;
    }

    public String getRequestUser() {
        return requestUser;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getModify() {
        return modify;
    }

    public void setModify(int modify) {
        this.modify = modify;
    }

    @Override
    public String toString() {
        return "ResponseSyncToDoMessage{" +
                "requestUser='" + requestUser + '\'' +
                ", todoID=" + todoID +
                ", resultCode=" + resultCode +
                ", modify=" + modify +
                '}';
    }
}
