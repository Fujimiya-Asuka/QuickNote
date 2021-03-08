package com.example.quicknote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.note_recycler_view);
        FloatingActionButton addBtn = findViewById(R.id.add_btn_main);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NoteCRUD noteCRUD = new NoteCRUD(this);
        List<Note> noteList = noteCRUD.getAllNotes();
        NoteAdapter adapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(adapter);
        //添加笔记按钮 跳转到编辑界面
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NoteCRUD noteCRUD = new NoteCRUD(this);
        List<Note> noteList = noteCRUD.getAllNotes();
        NoteAdapter adapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(adapter);
    }
}
