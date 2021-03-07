package com.example.quicknote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NoteCRUD {

    private final String table = "NOTE";
    private NoteDatabaseHelper noteDatabaseHelper;
    private SQLiteDatabase db;

    public NoteCRUD(Context context) {
        noteDatabaseHelper = new NoteDatabaseHelper(context);
    }

    //添加笔记
    public long addNote(String title, String data){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",data);
        long noteID = db.insert(table, null, contentValues); //返回一个主键的值

        contentValues.clear();
        db.close();
        return noteID;
    }

    //查找笔记

    //删除笔记

    //修改笔记
    public void upDataNote(long note_id,String title,String data){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",data);
        db.update(table,contentValues,"id=?",new String[]{""+note_id});
        db.close();
    }

//    //获取单个笔记
//    public Cursor getNote(long note_id){
//        db = noteDatabaseHelper.getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM NOTE WHERE id=? ", new String[]{"" + note_id});
//        return cursor;
//    }

    //获取所有的笔记
    public List<Note> getAllNotes(){
        db = noteDatabaseHelper.getWritableDatabase();
        List<Note> NoteList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM NOTE ORDER BY ID DESC",null);//将查询到的数据库信息以ID列表倒序排列
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

}
