package com.example.quicknote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    private Button writeDone_btn;
    private EditText note_title_edit,note_data_edit;
    private long note_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        writeDone_btn = findViewById(R.id.writeDone_btn);
        note_title_edit = findViewById(R.id.note_title_edit);
        note_data_edit = findViewById(R.id.note_data_edit);

        Intent intent = getIntent();


        note_id = intent.getLongExtra("Note_id",-1);
        if (note_id!=-1){
            SQLiteDatabase db = new NoteDatabaseHelper(EditActivity.this).getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM NOTE WHERE id=? ", new String[]{"" + note_id});
            while (cursor.moveToNext()){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String data = cursor.getString(cursor.getColumnIndex("data"));
                note_title_edit.setText(title);
                note_data_edit.setText(data);
            }
            cursor.close();
            db.close();
        }


        //将数据写入数据库
        writeDone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                NoteCRUD noteCRUD = new NoteCRUD(EditActivity.this);
                String title = note_title_edit.getText().toString();
                String data = note_data_edit.getText().toString();
                if ("".equals(title) || "".equals(data)){
                    Toast.makeText(EditActivity.this,"标题或内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(note_id>0){
                        noteCRUD.upDataNote(note_id,title,data);
                    }else {
                        noteCRUD.addNote(title,data);
                    }
                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}
