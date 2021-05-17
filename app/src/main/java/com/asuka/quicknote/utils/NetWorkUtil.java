package com.asuka.quicknote.utils;

import android.content.Context;
import android.media.Image;
import android.util.Log;

import com.asuka.quicknote.domain.Note;
import com.asuka.quicknote.domain.ToDo;
import com.asuka.quicknote.domain.User;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetWorkUtil {
    private String TAG = "NetWorkUtil";
    private String url = "http://8.129.51.177:8080/QuickNoteServlet";
//    private String url = "http://192.168.43.244:8080/QuickNoteServlet";
    private String user = null;

    public NetWorkUtil(Context context) {
        this.user = context.getSharedPreferences("config",Context.MODE_PRIVATE).getString("user",null);
    }

    /**
     * 登录
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 11:58
     * @param username
     * @param password
     * @return
     * 返回call
     */
    public Call login(String username,String password){
        //创建客户端
        OkHttpClient client = new OkHttpClient();
        //根据用户输入的用户名和密码创建User对象并转换为json
        String jsonStr = new Gson().toJson(new User(username,password));
        //设置传输数据类型
        MediaType mediaType = MediaType.parse("application/json");
        //设置请求体
        RequestBody requestBody = RequestBody.create(mediaType,jsonStr);
        Request request = new Request.Builder()
                .url(url+"/login")
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    /**
     * 注册
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 11:58
     * @param username
     * @param password
     * @return
     * 返回call
     */
    public Call register(String username,String password){
        //创建客户端
        OkHttpClient client = new OkHttpClient();
        //根据用户输入的用户名和密码创建User对象并转换为json
        String jsonStr = new Gson().toJson(new User(username,password));
        //设置传输数据类型
        MediaType mediaType = MediaType.parse("application/json");
        //设置请求体
        final RequestBody requestBody = RequestBody.create(mediaType,jsonStr);
        Request request = new Request.Builder()
                .url(url+"/register")
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    /**
     * 上传单个待办
     * @Author:  XuZhenHui
     * @Time:  2021/5/2 13:07
     * @param toDo
     * 待办对象
     * @return
     * 返回call
     */
    public Call uploadTodo(ToDo toDo){
        List<ToDo> toDoList = new ArrayList<>();
        toDoList.add(toDo);
       return uploadTodo(toDoList);
    }

    /**
     * 上传待办列表
     * @Author:  XuZhenHui
     * @Time:  2021/5/2 13:07
     * @param toDoList
     * 待办对象列表
     * @return
     * 返回call
     */
    public Call uploadTodo(List<ToDo> toDoList){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,new Gson().toJson(toDoList));
        Request request = new Request.Builder()
                .url(url+"/SyncServlet")
                .method("POST", body)
                .addHeader("user",user)
                .addHeader("method", "uploadToDo")
                .addHeader("Content-Type", "application/json")
                .build();
        return client.newCall(request);
    }

    /**
     * 发送请求删除服务器中的单个待办
     * @Author:  XuZhenHui
     * @Time:  2021/4/30 21:00
     * @param todoID
     * 要删除的待办ID
     * @return
     */
    public Call deleteToDo(int todoID){
        Log.d(TAG, "deleteToDo: "+todoID);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url+"/SyncServlet")
                .method("POST", body)
                .addHeader("user",user)
                .addHeader("method", "deleteToDo")
                .addHeader("todoID",""+todoID)
                .build();
        return client.newCall(request);
    }

    /**
     * 下载待办的方法
     * @Author:  XuZhenHui
     * @Time:  2021/4/26 16:12
     * @param modify
     * 本地待办最大时间戳
     */
    public Call downloadNotSyncTodo(int modify){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url+"/SyncServlet")
                .addHeader("user", user)
                .addHeader("method", "downloadToDo")
                .addHeader("modify", String.valueOf(modify))
                .build();
        return client.newCall(request);
    }



    /**
     * 上传单个便签
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 13:07
     * @param note
     * 便签对象
     * @return
     * 返回call
     */
    public Call uploadNote(Note note){
        List<Note> noteList = new ArrayList<>();
        noteList.add(note);
        return uploadNote(noteList);
    }

    /**
     * 上传便签列表
     * @Author:  XuZhenHui
     * @Time:  2021/5/2 13:07
     * @param noteList
     * 待办对象列表
     * @return
     * 返回call
     */
    public Call uploadNote(List<Note> noteList){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,new Gson().toJson(noteList));
        Request request = new Request.Builder()
                .url(url+"/SyncServlet")
                .method("POST", body)
                .addHeader("user",user)
                .addHeader("method", "uploadNote")
                .addHeader("Content-Type", "application/json")
                .build();
        return client.newCall(request);
    }

    /**
     * 发送请求删除服务器中的单个便签
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 21:00
     * @param noteID
     * 要删除的便签ID
     * @return
     */
    public Call deleteNote(int noteID){
        Log.d(TAG, "deleteToDo: "+noteID);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url+"/SyncServlet")
                .method("POST", body)
                .addHeader("user",user)
                .addHeader("method", "deleteNote")
                .addHeader("noteID",""+noteID)
                .build();
        return client.newCall(request);
    }

    /**
     * 下载便签的方法
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 16:12
     * @param modify
     * 本地便签最大时间戳
     */
    public Call downloadNotSyncNote(int modify){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url+"/SyncServlet")
                .addHeader("user", user)
                .addHeader("method", "downloadNote")
                .addHeader("modify", String.valueOf(modify))
                .build();
        return client.newCall(request);
    }

    /**
     * 上传图片的方法
     * @Author:  XuZhenHui
     * @Time:  2021/5/15 21:03
     * @return
     */
    public Call uploadImage(int noteID,String imagePath){
        Log.d(TAG, "uploadImage");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("upFile",noteID+".jpg",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(imagePath)))
                .build();
        Request request = new Request.Builder()
                .url(url+"/uploadImage?userName=666&noteID=2")
                .addHeader("user",user)
                .addHeader("noteID",""+noteID)
                .method("POST", body)
                .build();
        return client.newCall(request);
    }


//    public Call deleteToDo(List<Integer> todoIDList){
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("application/json");
//        String json = new Gson().toJson(todoIDList);
//        Log.d(TAG, "deleteToDo: 发送数据："+json);
//        RequestBody body = RequestBody.create(mediaType,json);
//        Request request = new Request.Builder()
//                .url(url+"/SyncServlet")
//                .method("POST", body)
//                .addHeader("user",user)
//                .addHeader("method", "deleteToDo")
//                .addHeader("Content-Type", "application/json")
//                .build();
//        return client.newCall(request);
//    }

}
