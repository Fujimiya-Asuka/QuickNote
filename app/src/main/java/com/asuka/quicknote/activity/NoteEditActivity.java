package com.asuka.quicknote.activity;

import androidx.appcompat.app.ActionBar;
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
    private long noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //在toolbar上显示返回按钮
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }

        note_title_edit = findViewById(R.id.todo_title_edit);
        note_data_edit = findViewById(R.id.todo_data_edit);

        Intent intent = getIntent();
        //获取noteID
        noteID = intent.getLongExtra("Note_id", 0);
        //noteID有效，展示数据
        if (noteID != 0) {
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


    //加载toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                //返回按钮
            case android.R.id.home:
                returnMainActivity();
                break;
                //提交按钮
            case R.id.submit_toolbarBtn_noteEdit:
                addOrUpdateNote();
                break;
                //删除按钮
            case R.id.delete_toolbarBtn_noteEdit:
                NoteCRUD noteCRUD = new NoteCRUD(mContent);
                if (noteID>0){
                    noteCRUD.deleteNote(noteID);
                    returnMainActivity();
                }else {
                    Toast.makeText(mContent,"不能删除不存在的标签哦",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void addOrUpdateNote() {
        String newNoteTitle = note_title_edit.getText().toString();
        String newNoteData = note_data_edit.getText().toString();
        String time = new Time(new Date()).getTime();
        NoteCRUD noteCRUD = new NoteCRUD(mContent);
        //是已存在的便签
        if (noteID > 0) {
            // 输入框发生了改变，执行更新操作
            if (!newNoteTitle.equals(oldNoteTitle) || !newNoteData.equals(oldNoteData)) {
                noteCRUD.upDateNote(noteID, newNoteTitle, newNoteData, time);
                noteCRUD.closeDB();
                returnMainActivity();
            }
        }
        //是新的便签
         else if (noteID==0) {
            //如果输入标题为空
            if ("".equals(newNoteTitle)) {
                Toast.makeText(NoteEditActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
            }
            //输入框不为空，执行添加操作
            else {
                noteCRUD.addNote(newNoteTitle, newNoteData, time);
                noteCRUD.closeDB();
                returnMainActivity();
            }
        }

    }

    private void returnMainActivity(){
        Intent intent = new Intent(mContent, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
