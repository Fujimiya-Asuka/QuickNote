package com.example.quicknote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.note_recycler_view);
        FloatingActionButton addBtn = findViewById(R.id.add_btn_main);


        NoteDatabaseHelper dbHelper = new NoteDatabaseHelper(this, "textNote.db", null, 1);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<Note> NoteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NOTE ORDER BY ID DESC",null);//将查询到的数据库信息以ID列表倒序排列
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            Note note = new Note(title,data);
            NoteList.add(note);
        }
        cursor.close();
        NoteAdapter adapter = new NoteAdapter(NoteList);
        recyclerView.setAdapter(adapter);

        //添加笔记按钮 跳转到编辑界面
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                finish();
            }
        });

//            //测试
//        search_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                List<Note> NoteList = new ArrayList<>();
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                @SuppressLint("Recycle") Cursor cursor= db.rawQuery("SELECT * FROM NOTE",null);
//                while (cursor.moveToNext()){
//                    String title = cursor.getString(cursor.getColumnIndex("title"));
//                    String data = cursor.getString(cursor.getColumnIndex("data"));
//                    Note note = new Note(title,data);
//                    NoteList.add(note);
//                }
//                NoteAdapter adapter = new NoteAdapter(NoteList);
//                recyclerView.setAdapter(adapter);
//            }
//        });

    }


}
