package com.asuka.quicknote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteDatabaseHelper extends SQLiteOpenHelper {


    private Context mContext;

    public NoteDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public NoteDatabaseHelper(Context context) {
        super(context,"textNote.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CRATE_NOTE = "create table NOTE (" +
                "id integer primary key autoincrement," +
                "title text," +
                "data text," +
                "time text)";
        sqLiteDatabase.execSQL(CRATE_NOTE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists NOTE");
        onCreate(sqLiteDatabase);
    }
}
