package com.asuka.quicknote.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.asuka.quicknote.domain.ToDo;
import java.util.ArrayList;
import java.util.List;

public class ToDoCRUD {

    private String tableName = null;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public ToDoCRUD(Context context) {
        this.tableName = context.getSharedPreferences("config",Context.MODE_PRIVATE).getString("todo_tableName",null);
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * 添加待办
     * @param title
     * 标题
     * @param time
     * 提醒时间
     * @param notify
     * 是否需要提醒以及提醒状态
     * -1：不提醒
     * 0：已完成
     * 1：待提醒
     * @return
     * //返回一个主键的值 todoID
     */
    public long addTodo(String title,String time,int notify){
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("time",time);
        contentValues.put("notify",notify);
        long todoID = db.insert(tableName, null, contentValues); //insert()返回一个主键的值
        contentValues.clear();
//        db.close();
        return todoID;
    }

    //查找待办，模糊匹配
    public List<ToDo> searchTodo(String s){
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        final String s1 = "SELECT * FROM "+tableName+" WHERE";
        final String s2 = " title LIKE "+ "'%" + s + "%'";
        final String s3 = " OR time LIKE "+ "'%" + s + "%'";
        Cursor cursor = db.rawQuery(s1+s2+s3,null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
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

    //删除指定待办
    public void removeTodo(long todoId){
        db = databaseHelper.getWritableDatabase();
        db.delete(tableName,"id=?",new String[]{""+todoId});
        db.close();
    }

    /**
     * 删除所有待办
     */
    public void removeAllTodo(){
        db = databaseHelper.getWritableDatabase();
        db.delete(tableName,null,null);
        db.close();
    }

    /**
     * 更新待办
     * @param todoId
     * @param title
     * @param time
     * @return
     * 返回被更新的待办ID
     */
    public long upDataTodo(long todoId,String title,String time,int notify){
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("time",time);
        contentValues.put("notify",notify);
        long todoID = db.update(tableName, contentValues, "id=?", new String[]{"" + todoId});
        db.close();
        return todoID;
    }

    /**
     * 获取单个笔记，
     * @param todo_id
     * 待办Id
     * @return
     * 返回note对象
     */
    public ToDo getTodo(long todo_id){
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName+" WHERE id=? ", new String[]{"" + todo_id});
        ToDo todo = new ToDo();
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            todo.setTitle(title);
            todo.setTime(time);
        }
        cursor.close();
        db.close();
        return todo;
    }

    //获取所有的待办
    public List<ToDo> getAllTodo(){
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName+" ORDER BY ID DESC",null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            todoList.add(new ToDo(todoId,title,time,notify));
        }
        cursor.close();
        db.close();
        return todoList;
    }


    /**
     * 获取所有需要提醒的待办
     * @return
     */
    public List<ToDo> getAllNotNotifyTodo(){
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName+" WHERE notify>0",null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int notify = cursor.getInt(cursor.getColumnIndex("notify"));
            todoList.add(new ToDo(todoId,title,time,notify));
        }
        cursor.close();
        db.close();
        return todoList;
    }


    /**
     * 设置待办的状态
     * @param toDoId
     *待办ID
     * @param notify
     * 状态/n
     * -1：不提醒
     * 0：已经提醒
     * 1：待提醒
     */
    public void setToDoNotify(long toDoId, int notify){
        db = databaseHelper.getWritableDatabase();
        db.execSQL("UPDATE "+ tableName +" SET notify =? WHERE id = ?",new String[]{""+notify,""+toDoId});
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("isDone",isDone);
//        db.update(table,contentValues,"id=?",new String[]{""+toDoId});
//        db.close();
    }

}
