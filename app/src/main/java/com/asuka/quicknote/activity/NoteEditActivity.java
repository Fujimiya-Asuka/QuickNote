package com.asuka.quicknote.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.asuka.quicknote.R;
import com.asuka.quicknote.db.NoteCRUD;
import com.asuka.quicknote.db.DatabaseHelper;
import com.asuka.quicknote.myClass.Time;

import java.util.Date;

public class NoteEditActivity extends AppCompatActivity {
    private final String TAG = "NoteEditActivity";
    private final Context mContent = NoteEditActivity.this;
    private EditText note_title_edit, note_data_edit;
    private String oldNoteTitle, oldNoteData;
    private long noteID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);

        note_title_edit = findViewById(R.id.todo_title_edit);
        note_data_edit = findViewById(R.id.todo_data_edit);

        Intent intent = getIntent();

        //获取noteID
        noteID = intent.getLongExtra("Note_id", -1);
        //noteID有效，展示数据
        if (noteID != -1) {
            SQLiteDatabase db = new DatabaseHelper(NoteEditActivity.this).getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM NOTE WHERE id=? ", new String[]{"" + noteID});
            while (cursor.moveToNext()) {
                oldNoteTitle = cursor.getString(cursor.getColumnIndex("title"));
                oldNoteData = cursor.getString(cursor.getColumnIndex("data"));
                note_title_edit.setText(oldNoteTitle);
                note_data_edit.setText(oldNoteData);
            }
            cursor.close();
            db.close();
        }


    }

    //重写返回按键
//    @Override
//    public void onBackPressed() {
//        String newNoteTitle = note_title_edit.getText().toString();
//        String newNoteData = note_data_edit.getText().toString();
//        //是已存在的便签
//        if (noteID > 0) {
//            if (!newNoteTitle.equals(oldNoteTitle) || !newNoteData.equals(oldNoteData)){
//                NoteCRUD noteCRUD = new NoteCRUD(mContent);
//                String time = new Time(new Date()).getTime();
//                noteCRUD.upDateNote(noteID, newNoteTitle, newNoteData, time);
//            }
//        }else {
//            if ("".equals(newNoteTitle) || "".equals(newNoteData)) {
//                Toast.makeText(NoteEditActivity.this, "标题或内容不能为空", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    //加载toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //提交按钮
            case R.id.submit_editView:
                addOrUpdateNote();
                startActivity(new Intent(mContent,MainActivity.class));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void addOrUpdateNote() {
        String newNoteTitle = note_title_edit.getText().toString();
        String newNoteData = note_data_edit.getText().toString();
        NoteCRUD noteCRUD = new NoteCRUD(mContent);
        String time = new Time(new Date()).getTime();
        //是已存在的便签
        if (noteID > 0) {
            // 输入框发生了改变，执行更新操作
            if (newNoteTitle.equals(oldNoteTitle) || newNoteData.equals(oldNoteData)) {
                noteCRUD.upDateNote(noteID, newNoteTitle, newNoteData, time);
            }else{
                return;
            }
        }
        //是新的便签
        else {
            //如果输入框为空
            if ("".equals(newNoteTitle) || "".equals(newNoteData)) {
                Toast.makeText(NoteEditActivity.this, "标题或内容不能为空", Toast.LENGTH_SHORT).show();
            }
            //输入框不为空，执行添加操作
            else {
                noteCRUD.addNote(newNoteTitle, newNoteData, time);
            }
        }
        noteCRUD.closeDB();
    }

}
