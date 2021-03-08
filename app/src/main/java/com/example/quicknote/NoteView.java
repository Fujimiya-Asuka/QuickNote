package com.example.quicknote;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collection;

public class NoteView extends AppCompatActivity {

    private EditText editText;
    private FloatingActionButton remove_btn;

    private long note_id=0;
    private Note note;
    private String oldTitle;
    private String oldData;
    private NoteCRUD noteCRUD = new NoteCRUD(NoteView.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar_note_view);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar_note_view);
        ImageView imageView = findViewById(R.id.app_bar_image_note_view);
        editText = findViewById(R.id.note_view_edit);
        remove_btn = findViewById(R.id.removeBtn_note_view);

        //获取被点击的note对象
        note_id = intent.getLongExtra("Note_id",-1);
        if (note_id!=-1){
            noteCRUD = new NoteCRUD(NoteView.this);
            note = noteCRUD.getNote(note_id);
            oldTitle = note.getTitle();
            oldData = note.getData();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        collapsingToolbar.setTitle(oldTitle);
        editText.setText(oldData);

        //删除笔记
        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteCRUD.removeNote(note_id);
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                //如果数据不相同，就更新
                if (!oldData.equals(editText.getText().toString())) {
                    NoteCRUD noteCRUD = new NoteCRUD(NoteView.this);
                    noteCRUD.upDataNote(note_id,oldTitle,editText.getText().toString());
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}