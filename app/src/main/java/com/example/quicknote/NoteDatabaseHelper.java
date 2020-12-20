package com.example.quicknote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String CRATE_NOTE = "create table NOTE (" +
            "id integer primary key autoincrement," +
            "title text," +
            "data text )";

    private Context mContext;

    public NoteDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext =context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CRATE_NOTE);
        Toast.makeText(mContext,"创建成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists NOTE");
        onCreate(sqLiteDatabase);
    }
}
