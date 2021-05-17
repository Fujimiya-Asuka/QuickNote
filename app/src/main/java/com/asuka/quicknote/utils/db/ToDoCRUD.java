package com.asuka.quicknote.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.LongDef;

import com.asuka.quicknote.domain.ToDo;
import java.util.ArrayList;
import java.util.List;

public class ToDoCRUD {

    private final String TAG = "ToDoCRUD";
    private String tableName = null;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public ToDoCRUD(Context context) {
        this.tableName = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("todo_tableName", null);
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * 添加待办
     *
     * @param title  标题
     * @param time   提醒时间
     * @param notify 是否需要提醒以及提醒状态
     *               -1：不提醒
     *               0：已完成
     *               1：待提醒
     * @return //返回一个主键的值 todoID
     */
    public long addTodoToLocal(String title, String time, int notify) {
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("time", time);
        contentValues.put("notify", notify);
        contentValues.put("state", 0);  //本地新增待办，state为0
        long todoID = db.insert(tableName, null, contentValues); //insert()返回一个主键的值
        contentValues.clear();
        db.close();
        return todoID;
    }

    /**
     * 查找待办，模糊匹配
     * @Author:  XuZhenHui
     * @Time:  2021/4/27 22:29
     * @param s
     * @return
     */
    public List<ToDo> searchTodo(String s) {
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        final String s1 = "SELECT * FROM " + tableName + " WHERE";
        final String s2 = " title LIKE " + "'%" + s + "%'";
        final String s3 = " OR time LIKE " + "'%" + s + "%'";
        Cursor cursor = db.rawQuery(s1 + s2 + s3, null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()) {
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            ToDo toDo = new ToDo(todoId, title, time, notify);
            todoList.add(toDo);
        }
        cursor.close();
        db.close();
        return todoList;
    }

    /**
     * 删除指定id的待办
     *
     * @param todoId 待办ID
     */
    public void removeTodo(long todoId) {
        db = databaseHelper.getWritableDatabase();
        db.delete(tableName, "id=?", new String[]{"" + todoId});
        db.close();
    }

    /**
     * 删除所有待办
     */
    public void removeAllTodo() {
        db = databaseHelper.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    /**
     * 更新待办
     *
     * @param todoId
     * @param title
     * @param time
     * @return 返回被更新的待办ID
     */
    public long upDataTodo(long todoId, String title, String time, int notify) {
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("time", time);
        contentValues.put("notify", notify);
        contentValues.put("state", 1); //本地修改待办，state为1
        long todoID = db.update(tableName, contentValues, "id=?", new String[]{"" + todoId});
        db.close();
        return todoID;
    }

    /**
     * <h1>获取指定ID的单个待办</h1>
     *
     * @param todo_id
     * 待办ID
     * @return
     * 返回待办对象
     */
    public ToDo getTodo(long todo_id) {
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE id=? ", new String[]{"" + todo_id});
        ToDo todo = new ToDo();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            int modify = cursor.getInt(cursor.getColumnIndex("modify"));
            todo.setId(id);
            todo.setTitle(title);
            todo.setTime(time);
            todo.setNotify(notify);
            todo.setState(state);
            todo.setModify(modify);
        }
        cursor.close();
        db.close();
        return todo;
    }

    /**
     * <h1>获取所有未删除的待办</h1>
     * @return 返回TodoList<ToDo>
     */
    public List<ToDo> getAllTodo() {
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName +" WHERE state>-1 ORDER BY ID DESC", null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()) {
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            int modify = cursor.getInt(cursor.getColumnIndex("modify"));
            todoList.add(new ToDo(todoId, title, time, notify,state,modify));
        }
        cursor.close();
        db.close();
        return todoList;
    }


    /**
     * 获取所有需要提醒的待办
     *
     * @return
     */
    public List<ToDo> getAllNotNotifyTodo() {
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE notify>0", null);
        while (cursor.moveToNext()) {
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            todoList.add(new ToDo(todoId, title, time, notify));
        }
        cursor.close();
        db.close();
        return todoList;
    }


    /**
     * 设置待办的提醒状态
     * @param toDoId 待办ID
     * @param notify 状态/n
     *               -1：不提醒
     *               0：已经提醒
     *               1：待提醒
     */
    public void setToDoNotify(long toDoId, int notify) {
        db = databaseHelper.getWritableDatabase();
        db.execSQL("UPDATE " + tableName + " SET notify =?,state=? WHERE id = ?", new String[]{"" + notify,""+1,"" + toDoId});
        db.close();
    }

    /**
     * 设置待办的同步状态
     * @Author:  XuZhenHui
     * @Time:  2021/4/28 18:21
     * @param toDoId
     * 待办ID
     * @param state
     * 状态
     *  -1 ：本地已删除
     *   0  ：本地新增
     *   1 ：本地修改
     *   2 ：已同步
     */
    public void setToDoState(long toDoId, int state){
        db = databaseHelper.getWritableDatabase();
        db.execSQL("UPDATE " + tableName + " SET state =? WHERE id = ?", new String[]{"" + state, "" + toDoId});
        db.close();
    }


    /**
     * 获取所有未同步的待办
     * @Author:  XuZhenHui
     * @Time:  2021/4/22 22:45
     * @return
     * 返回ToDo对象列表
     */
    public List<ToDo> getAllNotSyncTodo() {
        List<ToDo> toDoList = new ArrayList<>();
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE state<2", null);
        while (cursor.moveToNext()){
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            int modify = cursor.getInt(cursor.getColumnIndex("modify"));
            toDoList.add(new ToDo(todoId, title, time, notify,state,modify));
        }
        db.close();
        return toDoList;
    }

    /**
     * 更新待办的State和modify
     * @Author:  XuZhenHui
     * @Time:  2021/4/22 22:59
     * @param todoID
     * 待办ID
     * @param state
     * 状态
     * @param modify
     * 时间戳
     */
    public void updateToDoStateModify(int todoID,int state,int modify){
        Log.d(TAG, "updateToDoStateModify: "+todoID+state+modify);
        db=databaseHelper.getWritableDatabase();
        db.execSQL("UPDATE "+tableName+" SET state = ?,modify = ? WHERE id = ? ;",new String[]{""+state,""+modify,""+todoID});
        db.close();
    }


    /**
     * 把从网络上获取到的待办添加到本地数据库
     * @Author:  XuZhenHui
     * @Time:  2021/4/26 17:02
     * @param toDoList
     *待办的List对象
     * @return
     * 返回成功添加待办的数量，用来提示用户成功获取到了几条待办
     */
    public int addTodoToLocal(List<ToDo> toDoList) {
        db = databaseHelper.getWritableDatabase();
        int addCount = 0;
        if (toDoList.size()>0){
            for (ToDo toDo : toDoList) {
                Log.d(TAG, "addTodo: "+toDo.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put("id",toDo.getId());
                contentValues.put("title", toDo.getTitle());
                contentValues.put("time", toDo.getTime());
                contentValues.put("notify", toDo.getNotify());
                contentValues.put("state", 2);  //从网络上同步下来的待办，state为2：已同步
                contentValues.put("modify", toDo.getModify());
                db.insert(tableName, null, contentValues);
                addCount++;
                contentValues.clear();
        }
        }
        db.close();
        return addCount;
    }

    /**
     * 获取本地最大待办时间戳
     * @Author:  XuZhenHui
     * @Time:  2021/4/27 18:31
     * @return
     */
    public int getLocalToDoMaxModify(){
        db = databaseHelper.getWritableDatabase();
        int maxModify = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(modify) FROM "+tableName, null);
        while (cursor.moveToNext()){
            maxModify = cursor.getInt(0);
        }
        db.close();
        return maxModify;
    }

}
