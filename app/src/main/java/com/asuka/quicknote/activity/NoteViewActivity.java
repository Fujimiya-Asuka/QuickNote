package com.asuka.quicknote.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asuka.quicknote.R;
import com.asuka.quicknote.domain.Note;
import com.asuka.quicknote.utils.db.NoteCRUD;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteViewActivity extends AppCompatActivity {
    private final String TAG = "NoteViewActivity";
    private Context mContent = NoteViewActivity.this;
    private String user = null;
    private long noteID = 0;
    private Note note;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        Intent intent = getIntent();
        user=getSharedPreferences("config",Context.MODE_PRIVATE).getString("user",null);
        Toolbar toolbar = findViewById(R.id.toolbar_note_view);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar_note_view);
        imageView = findViewById(R.id.app_bar_image_note_view);


        TextView textView = findViewById(R.id.note_view_edit);
        FloatingActionButton editBtn = findViewById(R.id.removeBtn_note_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //获取被点击的note对象
        noteID = intent.getLongExtra("Note_id",-1);
        if (noteID !=-1){
            NoteCRUD noteCRUD = new NoteCRUD(NoteViewActivity.this);
            note = noteCRUD.getNote(noteID);
            //设置标题
            collapsingToolbar.setTitle(note.getTitle());
            //设置文本
            textView.setText(note.getData());
        }

        //跳转编辑笔记，传递NoteID
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContent, NoteEditActivity.class).putExtra("Note_id", noteID));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noteID>0){
            Log.d(TAG, "noteImage: "+note.getImage());
            if ("1".equals(note.getImage())){
                Glide.with(this).load("http://8.129.51.177:8080/QuickNoteServlet/image/"+user+"/"+noteID+".jpg").into(imageView);
            }
        }
    }

    //左上角返回按键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //重写返回方法
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}