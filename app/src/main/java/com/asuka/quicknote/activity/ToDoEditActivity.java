package com.asuka.quicknote.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.asuka.quicknote.R;
import com.asuka.quicknote.db.ToDoCRUD;
import com.asuka.quicknote.myClass.Time;
import com.asuka.quicknote.myClass.ToDo;

import java.util.Calendar;
import java.util.Date;

public class ToDoEditActivity extends AppCompatActivity {
    private final Context mContext = ToDoEditActivity.this;
    private final String TAG = "ToDoEditActivity";
    private Toolbar toolbar;
    private EditText toDoTileEdit,toDoDataEdit;
    private ToDoCRUD toDoCRUD = new ToDoCRUD(mContext);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);
        toolbar = findViewById(R.id.toolbar_ToDoEdit);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }
        toDoTileEdit = findViewById(R.id.todo_title_edit);
        toDoDataEdit = findViewById(R.id.todo_data_edit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long todoID =getIntent().getLongExtra("todoID", -1);
        Log.d(TAG, "onResume: "+todoID);
        if (todoID!=-1){
            ToDo todo = toDoCRUD.getTodo(todoID);
            toDoTileEdit.setText(todo.getTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_todo_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //返回按钮
            case android.R.id.home :
                returnMainActivity();
            //提交按钮
            case R.id.submitBtn_toDoToolbar:
                toDoCRUD.addTodo(toDoTileEdit.getText().toString(),new Time(new Date()).getTime());
                returnMainActivity();
                break;
            case R.id.setNotify_toDoToolbar:
                Log.d(TAG, "onOptionsItemSelected: ");
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);//获取当前年
                int month = calendar.get(Calendar.MONTH);//获取当前月
                int day = calendar.get(Calendar.DAY_OF_MONTH);//获取当前日

                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, final int year, int month, int day) {
                        final Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(Calendar.YEAR,year);
                        calendar2.set(Calendar.MONTH,month);
                        calendar2.set(Calendar.DAY_OF_MONTH,day);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);//获取当前小时
                        int minute = calendar.get(Calendar.MINUTE);//获取当前分钟
                        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            //按下确定时的方法
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                Calendar calendar3 = Calendar.getInstance();
                                calendar3.set(Calendar.HOUR_OF_DAY,hour);
                                calendar3.set(Calendar.MINUTE,minute);
                                Log.d(TAG, "onTimeSet: "+calendar2.get(Calendar.YEAR));
                                Log.d(TAG, "onTimeSet: "+(calendar2.get(Calendar.MONTH)+1));
                                Log.d(TAG, "onTimeSet: "+calendar2.get(Calendar.DAY_OF_MONTH));
                                Log.d(TAG, "onTimeSet: "+calendar3.get(Calendar.HOUR_OF_DAY));
                                Log.d(TAG, "onTimeSet: "+calendar3.get(Calendar.MINUTE));
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,onTimeSetListener,hour,minute,true);
                        timePickerDialog.show();//显示timePickerDialog
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,onDateSetListener,year,month,day);
                datePickerDialog.show();//显示DatePickerDialog



                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: ");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        returnMainActivity();
    }

    private void returnMainActivity(){
        Intent intent = new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
    }
}