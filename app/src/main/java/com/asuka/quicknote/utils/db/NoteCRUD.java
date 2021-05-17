package com.asuka.quicknote.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.asuka.quicknote.domain.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteCRUD {
    private final String TAG = "NoteCRUD";
    private  String tableName = null;
    private DatabaseHelper noteDatabaseHelper;
    private SQLiteDatabase db;


    public NoteCRUD(Context context) {
        this.tableName = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("note_tableName",null);
        noteDatabaseHelper = new DatabaseHelper(context);
    }

    /**
     * 添加待办
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 14:52
     * @param title
     * 标题
     * @param data
     * 内容
     * @param image
     * 图像
     * @param time
     * 时间
     * @return
     * 返回noteID
     */
    public long addNote(String title, String data, String image,String time){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        contentValues.put("data",data);
        contentValues.put("image",image);
        contentValues.put("time",time);
        contentValues.put("state",0);//本地新增，state为0
        long noteID = db.insert(tableName, null, contentValues); //insert()返回一个主键的值
        contentValues.clear();
        db.close();
        return noteID;
    }

    /**
     * 查找笔记，模糊匹配
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 14:56
     * @param s
     * 需要查找的内容
     * @return
     * 返回Note对象的List列表
     */
    public List<Note> searchNotes(String s){
        db = noteDatabaseHelper.getWritableDatabase();
        List<Note> NoteList = new ArrayList<>();
        final String s1 = "SELECT * FROM "+tableName+" WHERE";
        final String s2 = " title LIKE "+ "'%" + s + "%'";
        final String s3 = " OR data LIKE "+ "'%" + s + "%'";
        Cursor cursor = db.rawQuery(s1+s2+s3,null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            long noteId = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            Note note = new Note(title,data,noteId,time);
            NoteList.add(note);
        }
        cursor.close();
        db.close();
        return NoteList;
    }


    /**
     * 删除本地指定ID的便签
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 14:57
     * @param note_id
     * noteID
     */
    public void deleteNote(long note_id){
        db = noteDatabaseHelper.getWritableDatabase();
        db.delete(tableName,"id=?",new String[]{""+note_id});
        db.close();
    }

    /**
     * 删除本地所有笔记
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 14:58
     */
    public void removeAllNote(){
        db = noteDatabaseHelper.getWritableDatabase();
        db.delete(tableName,null,null);
        db.close();
    }

    /**
     * 更新指定ID的便签
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 14:58
     * @param note_id
     * 便签ID
     * @param newTitle
     * 新标题
     * @param newData
     * 新内容
     * @param newImage
     * 新图像
     * @param newTime
     * 新修改的时间
     */
    public void upDateNote(long note_id, String newTitle, String newData,String newImage, String newTime){
        db = noteDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",newTitle);
        contentValues.put("data",newData);
        contentValues.put("image",newImage);
        contentValues.put("time",newTime);
        contentValues.put("state",1); //本地更新 state为1
        db.update(tableName,contentValues,"id=?",new String[]{""+note_id});
        db.close();
    }

    //获取单个笔记，返回note对象
    public Note getNote(long note_id){
        db = noteDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName+" WHERE id=? ", new String[]{"" + note_id});
        Note note = new Note();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int state  = cursor.getInt(cursor.getColumnIndex("state"));
            int modify  = cursor.getInt(cursor.getColumnIndex("modify"));
            note.setId(id);
            note.setTitle(title);
            note.setData(data);
            note.setImage(image);
            note.setTime(time);
            note.setState(state);
            note.setModify(modify);
        }
        cursor.close();
        db.close();
        return note;
    }

    //获取所有的笔记
    public List<Note> getAllNotes(){
        db = noteDatabaseHelper.getWritableDatabase();
        List<Note> NoteList = new ArrayList<>(50);
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName+" ORDER BY ID DESC",null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            long id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int state  = cursor.getInt(cursor.getColumnIndex("state"));
            int modify  = cursor.getInt(cursor.getColumnIndex("modify"));
            NoteList.add(new Note(id,title,data,image,time,state,modify));
        }
        cursor.close();
        db.close();
        return NoteList;
    }

    /**
     * 更新指定ID便签的state和modify
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 15:19
     * @param noteID
     * @param state
     * 状态
     * 0：本地新增
     * 1：本地修改
     * 2：已同步
     * @param modify
     * 时间戳
     */
    public void updateNoteStateModify(int noteID,int state,int modify){
        Log.d(TAG, "updateNoteStateModify: "+noteID+state+modify);
        db=noteDatabaseHelper.getWritableDatabase();
        db.execSQL("UPDATE "+tableName+" SET state = ?,modify = ? WHERE id = ? ;",new String[]{""+state,""+modify,""+noteID});
        db.close();
    }

    /**
     * 添加便签到本地
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 15:29
     * @param noteList
     * 获取到的note列表
     * @return
     * 返回成功添加的数量
     */
    public int addNoteToLocal(List<Note> noteList) {
        db = noteDatabaseHelper.getWritableDatabase();
        int addCount = 0;
        if (noteList.size()>0){
            for (Note note : noteList) {
                Log.d(TAG, "addTodo: "+note.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put("id",note.getId());
                contentValues.put("title", note.getTitle());
                contentValues.put("data", note.getData());
                contentValues.put("image", note.getImage());
                contentValues.put("time", note.getTime());
                contentValues.put("state", 2);  //从网络上同步下来的，state为2：已同步
                contentValues.put("modify", note.getModify());
                db.insert(tableName, null, contentValues);
                addCount++;
                contentValues.clear();
            }
        }
        db.close();
        return addCount;
    }

    /**
     * 获取本地便签最大的时间戳
     * @Author:  XuZhenHui
     * @Time:  2021/5/9 15:31
     * @return
     * 返回本地便签最大时间戳
     */
    public int getLocalNoteMaxModify(){
        db = noteDatabaseHelper.getWritableDatabase();
        int maxModify = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(modify) FROM "+tableName, null);
        while (cursor.moveToNext()){
            maxModify = cursor.getInt(0);
        }
        db.close();
        return maxModify;
    }

    //关闭数据库
    public void closeDB(){
        noteDatabaseHelper.close();
    }

}
