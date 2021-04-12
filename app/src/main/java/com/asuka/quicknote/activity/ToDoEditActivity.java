package com.asuka.quicknote.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;

import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.TimeSelectDialog;
import com.asuka.quicknote.utils.MyNotificationManager;
import com.asuka.quicknote.utils.MyNotification;
import com.asuka.quicknote.utils.NotifyService;
import com.asuka.quicknote.utils.db.ToDoCRUD;
import com.asuka.quicknote.domain.Time;
import com.asuka.quicknote.domain.ToDo;
import java.text.SimpleDateFormat;
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //返回按钮
            case android.R.id.home :
                returnMainActivity();
                break;
            //提交按钮
            case R.id.submitBtn_toDoToolbar:
                toDoCRUD.addTodo(toDoTileEdit.getText().toString(),new Time(new Date()).getTime());
                returnMainActivity();
                break;
            case R.id.setNotify_toDoToolbar:
                Log.d(TAG, "onOptionsItemSelected: ");
                final TimeSelectDialog timeSelectDialog = new TimeSelectDialog(mContext);
                timeSelectDialog.show();
                timeSelectDialog.setClickListener(new TimeSelectDialog.ClickListener() {
                    @Override
                    public void doConfirm(Calendar calendar) {
                        String time = new SimpleDateFormat("yyyy年MM月dd日hh时mm分").format(calendar.getTime());
                        Log.d(TAG, "doConfirm: "+time);
                        //requestCode用于唯一标识定时任务,不可重复，否则定时任务将会被覆盖，使用todoId替换
                        PendingIntent pendingIntent = PendingIntent.getService(mContext,1,new Intent(mContext, NotifyService.class),0);
                        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                        timeSelectDialog.dismiss();
                    }
                    @Override
                    public void doCancel() {
                        timeSelectDialog.dismiss();
                    }
                });
                break;
            default:
                Log.d(TAG, "");
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