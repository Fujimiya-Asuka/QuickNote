package com.asuka.quicknote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.asuka.quicknote.myClass.ToDo;
import java.util.ArrayList;
import java.util.List;

public class ToDoCRUD {

    private final String table = "TODO";
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public ToDoCRUD(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    //添加待办
    public long addTodo(String title,String time){
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("time",time);
        contentValues.put("isDone",0);
        long todoID = db.insert(table, null, contentValues); //insert()返回一个主键的值
        contentValues.clear();
        return todoID;
    }

    //查找待办，模糊匹配
    public List<ToDo> searchTodo(String s){
        db = databaseHelper.getWritableDatabase();
        List<ToDo> todoList = new ArrayList<>(50);
        final String s1 = "SELECT * FROM TODO WHERE";
        final String s2 = " title LIKE "+ "'%" + s + "%'";
        final String s3 = " OR time LIKE "+ "'%" + s + "%'";
        Cursor cursor = db.rawQuery(s1+s2+s3,null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int isDone = cursor.getInt(cursor.getColumnIndex("isDone"));
            ToDo toDo = new ToDo(todoId, title, time, isDone);
            todoList.add(toDo);
        }
        cursor.close();
        db.close();
        return todoList;
    }

    //删除指定待办
    public void removeTodo(long todoId){
        db = databaseHelper.getWritableDatabase();
        db.delete(table,"id=?",new String[]{""+todoId});
        db.close();
    }

    //删除所有待办
    public void removeAllTodo(){
        db = databaseHelper.getWritableDatabase();
        db.delete(table,null,null);
        db.close();
    }

    //修改待办
    public void upDataTodo(long todoId,String title,String time){
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",time);
        db.update(table,contentValues,"id=?",new String[]{""+todoId});
        db.close();
    }

    //获取单个笔记，返回note对象
    public ToDo getTodo(long note_id){
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TODO WHERE id=? ", new String[]{"" + note_id});
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
        Cursor cursor = db.rawQuery("SELECT * FROM TODO ORDER BY ID DESC",null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            long todoId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int isDone = cursor.getInt(cursor.getColumnIndex("time"));
            todoList.add(new ToDo(todoId,title,time,isDone));
        }
        cursor.close();
        db.close();
        return todoList;
    }

    //关闭数据库
    public void closeDB(){
        db.close();
    }
}
