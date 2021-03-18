package com.asuka.quicknote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private final String TAG = "DatabaseHelper";
    
    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context,"myData.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CRATE_NOTE = "create table NOTE (" +
                "id integer primary key autoincrement," +
                "title text," +
                "data text," +
                "time text)";
        sqLiteDatabase.execSQL(CRATE_NOTE);
        String CRATE_TODO = "create table TODO (" +
                "id integer primary key autoincrement," +
                "title text," +
                "time text,"+
                "isDone integer)";
        sqLiteDatabase.execSQL(CRATE_TODO);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists NOTE");
        onCreate(sqLiteDatabase);
    }
}
