package com.asuka.quicknote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.asuka.quicknote.myClass.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteCRUD {

    private final String table = "NOTE";
    private NoteDatabaseHelper noteDatabaseHelper;
    private SQLiteDatabase db;

    public NoteCRUD(Context context) {
        noteDatabaseHelper = new NoteDatabaseHelper(context);
    }

    //添加笔记
    public long addNote(String title, String data, String time){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",data);
        contentValues.put("time",time);
        long noteID = db.insert(table, null, contentValues); //insert()返回一个主键的值
        contentValues.clear();
        return noteID;
    }

    public long addNote(String title, String data){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",data);
        long noteID = db.insert(table, null, contentValues); //insert()返回一个主键的值
        contentValues.clear();
        db.close();
        return noteID;
    }

    //查找笔记，模糊匹配
    public List<Note> searchNotes(String s){
        db = noteDatabaseHelper.getWritableDatabase();
        List<Note> NoteList = new ArrayList<>();
        final String s1 = "SELECT * FROM NOTE WHERE";
        final String s2 = " title LIKE "+ "'%" + s + "%'";
        final String s3 = " OR data LIKE "+ "'%" + s + "%'";
        Cursor cursor = db.rawQuery(s1+s2+s3,null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            long noteId = cursor.getInt(cursor.getColumnIndex("id"));
            Note note = new Note(title,data,noteId);
            NoteList.add(note);
        }
        cursor.close();
        db.close();
        return NoteList;
    }

    //删除指定笔记
    public void removeNote(long note_id){
        db = noteDatabaseHelper.getWritableDatabase();
        db.delete(table,"id=?",new String[]{""+note_id});
        db.close();
    }

    //删除所有笔记
    public void removeAllNotes(String table){
        db = noteDatabaseHelper.getWritableDatabase();
        db.delete(table,null,null);
        db.close();
    }

    //修改笔记
    public void upDataNote(long note_id,String title,String data){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",data);
        db.update(table,contentValues,"id=?",new String[]{""+note_id});
        db.close();
    }

    //获取单个笔记，返回note对象
    public Note getNote(long note_id){
        db = noteDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NOTE WHERE id=? ", new String[]{"" + note_id});
        Note note = new Note();
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            note.setTitle(title);
            note.setData(data);
        }
        cursor.close();
        db.close();
        return note;
    }

    //获取所有的笔记
    public List<Note> getAllNotes(){
        db = noteDatabaseHelper.getWritableDatabase();
        List<Note> NoteList = new ArrayList<>(50);
        Cursor cursor = db.rawQuery("SELECT * FROM NOTE ORDER BY ID DESC",null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            long noteId = cursor.getInt(cursor.getColumnIndex("id"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            NoteList.add(new Note(title,data,noteId,time));
        }
        cursor.close();
        db.close();
        return NoteList;
    }

    //关闭数据库
    public void closeDB(){
        db.close();
    }

}
