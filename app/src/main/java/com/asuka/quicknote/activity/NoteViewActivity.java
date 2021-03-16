package com.asuka.quicknote.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuka.quicknote.R;
import com.asuka.quicknote.myClass.Note;
import com.asuka.quicknote.db.NoteCRUD;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteViewActivity extends AppCompatActivity {

    private TextView textView;
    private FloatingActionButton remove_btn;

    private long note_id=0;
    private Note note;
    private String oldTitle;
    private String oldData;
    private NoteCRUD noteCRUD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar_note_view);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar_note_view);
        ImageView imageView = findViewById(R.id.app_bar_image_note_view);
        textView = findViewById(R.id.note_view_edit);
        remove_btn = findViewById(R.id.removeBtn_note_view);
        noteCRUD = new NoteCRUD(NoteViewActivity.this);

        //获取被点击的note对象
        note_id = intent.getLongExtra("Note_id",-1);
        if (note_id!=-1){
            noteCRUD = new NoteCRUD(NoteViewActivity.this);
            note = noteCRUD.getNote(note_id);
            oldTitle = note.getTitle();
            oldData = note.getData();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置标题
        collapsingToolbar.setTitle(oldTitle);
        //设置文本
        textView.setText(oldData);

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
                saveNewData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //重写返回方法
    @Override
    public void onBackPressed() {
        saveNewData();
        super.onBackPressed();
    }

    //保存新数据
    private void saveNewData(){
        if (!oldData.equals(textView.getText().toString())) {
            NoteCRUD noteCRUD = new NoteCRUD(NoteViewActivity.this);
            noteCRUD.upDataNote(note_id,oldTitle,textView.getText().toString());
        }
        finish();
    }
}