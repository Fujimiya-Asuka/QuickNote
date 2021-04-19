package com.asuka.quicknote.utils.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private final String TAG = "DatabaseHelper";
    private String note_tableName;
    private String todo_tableName;
    private Context mContext;
    
    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context,"myData.db",null,1);
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        mContext=context;
        note_tableName = sharedPreferences.getString("note_tableName",null);
        todo_tableName = sharedPreferences.getString("todo_tableName",null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: ");
        String CRATE_NOTE = "create table "+note_tableName+" (" +
                "id integer primary key autoincrement," +
                "title text," +
                "data text," +
                "time text)";
        sqLiteDatabase.execSQL(CRATE_NOTE);
        String CRATE_TODO = "create table "+todo_tableName+" (" +
                "id integer primary key autoincrement," +
                "title text," +
                "time text,"+
                "notify integer)";
        sqLiteDatabase.execSQL(CRATE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists NOTE");
        sqLiteDatabase.execSQL("drop table if exists TODO");
        onCreate(sqLiteDatabase);
    }

    /**
     * 删除数据库（删除文件）
     */
    public void deleteDataBase(){
        mContext.deleteDatabase("myData.db");
    }


}
