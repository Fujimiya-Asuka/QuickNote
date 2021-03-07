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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        writeDone_btn = findViewById(R.id.writeDone_btn);
        note_title_edit = findViewById(R.id.note_title_edit);
        note_data_edit = findViewById(R.id.note_data_edit);

        Intent intent = getIntent();
        long note_id = intent.getLongExtra("Note_id",-1);
        if (note_id!=-1){
            NoteCRUD noteCRUD = new NoteCRUD(EditActivity.this);
            Cursor cursor = noteCRUD.getNote(note_id);
            while (cursor.moveToNext()){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                note_title_edit.setText(title);
            }

        }



        writeDone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SQLiteDatabase db = dbHelper.getReadableDatabase();
//                ContentValues values = new ContentValues();
//                values.put("title",note_title_edit.getText().toString());
//                values.put("data",note_data_edit.getText().toString());
//                db.insert("NOTE",null,values);
//                values.clear();
                NoteCRUD noteCRUD = new NoteCRUD(EditActivity.this);
                noteCRUD.addNote(note_title_edit.getText().toString(), note_data_edit.getText().toString());
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
