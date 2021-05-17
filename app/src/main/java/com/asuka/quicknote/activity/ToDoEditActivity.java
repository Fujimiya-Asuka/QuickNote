package com.asuka.quicknote.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.TimeSelectDialog;
import com.asuka.quicknote.utils.AlarmUtil;
import com.asuka.quicknote.utils.NetWorkUtil;
import com.asuka.quicknote.utils.db.ToDoCRUD;
import com.asuka.quicknote.utils.TimeUtil;
import com.asuka.quicknote.domain.ToDo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ToDoEditActivity extends AppCompatActivity {
    private final Context mContext = ToDoEditActivity.this;
    private final String TAG = "ToDoEditActivity";
    private Date notifyTime =new Date();
    private Toolbar toolbar;
    private EditText todoTileEdit;
    private TextView toDoNotifyTime;
    private int todoNotify = -1;
    private long todoID;
    private String oldToDoTitle;
    private String oldToDoTime;
    private int oldToDoNotify;
    private String newToDoTime;


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
        todoTileEdit = findViewById(R.id.todo_title_edit);
        toDoNotifyTime = findViewById(R.id.todo_notifyTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
       //接收todoID
        todoID =getIntent().getLongExtra("todoID", -1);
        Log.d(TAG, "onResume: "+todoID);
        //判断是否是已存在的待办,是已经存在的待办则将其展示
        if (todoID!=-1){
            ToDo todo = new ToDoCRUD(mContext).getTodo(todoID);
            oldToDoTitle=todo.getTitle();
            oldToDoTime=todo.getTime();
            newToDoTime=todo.getTime();
            oldToDoNotify=todo.getNotify();
            todoTileEdit.setText(oldToDoTitle);
            toDoNotifyTime.setText("提醒时间"+oldToDoTime);
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
                String newToDoTitle = todoTileEdit.getText().toString();
                //1.1如果是新的待办则执行添加操作
                if(todoID==-1){
                    //1.1.1不允许空插入，弹出Toast
                    if ("".equals(todoTileEdit.getText().toString())){
                        Toast.makeText(mContext,"待办内容不能为空",Toast.LENGTH_SHORT).show();
                    }
                    //1.1.2检查到不为空，执行插入
                    else {
                        //不带设置提醒的插入
                        final int todoId = (int) new ToDoCRUD(mContext).addTodoToLocal(newToDoTitle, new TimeUtil(notifyTime).getTimeString(), todoNotify);
                        //如果待办需要设置提醒，则立即为其设置提醒
                        if (todoNotify==1){
                            AlarmUtil.setAlarm(mContext,todoId,notifyTime);
                        }
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    new NetWorkUtil(mContext).addTodo(new ToDoCRUD(mContext).getTodo(todoId)).execute();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start()
                        new NetWorkUtil(mContext).uploadTodo(new ToDoCRUD(mContext).getTodo(todoId)).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                // todo 此处可能会获取不到信息，要try
                                int resultCode = Integer.parseInt(response.header("resultCode"));
                                int todoID = Integer.parseInt(response.header("todoID"));
                                int modify = Integer.parseInt(response.header("modify"));
                                Log.d(TAG, "resultCode："+resultCode);
                                Log.d(TAG, "todoID："+todoID);
                                Log.d(TAG, "modify："+modify);
                                if (resultCode==1){
                                    new ToDoCRUD(getApplicationContext()).updateToDoStateModify(todoID,2,modify);
                                }

//                                new ToDoCRUD(getApplicationContext()).updateToDoStateModify();
                            }
                        });

                        returnMainActivity();
                    }
                }
                //1.2如果是已存在的待办，执行更新操作
                else {
                    if ("".equals(newToDoTitle)){
                        Toast.makeText(mContext,"待办内容不能为空",Toast.LENGTH_SHORT).show();
                    }else if (!oldToDoTitle.equals(newToDoTitle) || !oldToDoTime.equals(newToDoTime)){
                        if (oldToDoNotify<1){
                            new ToDoCRUD(mContext).upDataTodo(todoID,newToDoTitle,newToDoTime,oldToDoNotify);
                        }else {
                            new ToDoCRUD(mContext).upDataTodo(todoID,newToDoTitle,newToDoTime,todoNotify);
                            AlarmUtil.setAlarm(mContext,(int) todoID,notifyTime);
                        }
                        new NetWorkUtil(getApplicationContext()).uploadTodo(new ToDoCRUD(getApplicationContext()).getTodo(todoID)).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                int resultCode = Integer.parseInt(response.header("resultCode"));
                                int todoID = Integer.parseInt(response.header("todoID"));
                                int modify = Integer.parseInt(response.header("modify"));
                                Log.d(TAG, "resultCode："+resultCode);
                                Log.d(TAG, "todoID："+todoID);
                                Log.d(TAG, "modify："+modify);
                                if (resultCode==1){
                                    new ToDoCRUD(getApplicationContext()).updateToDoStateModify(todoID,2,modify);
                                }
                            }
                        });
                        returnMainActivity();
                    }else {
                        //内容无变化，直接返回
                        returnMainActivity();
                    }
                }
                break;
                //设置提醒的时间
            case R.id.setNotify_toDoToolbar:
                Log.d(TAG, "onOptionsItemSelected: ");
                setNotifyTime();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        returnMainActivity();
    }

    /**
     * 返回主页
     */
    private void returnMainActivity(){
        Intent intent = new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 弹出设置提醒时间对话框设置时间
     */
    private void setNotifyTime(){
        final TimeSelectDialog timeSelectDialog = new TimeSelectDialog(mContext);
        timeSelectDialog.show();
        timeSelectDialog.setClickListener(new TimeSelectDialog.ClickListener() {
            @Override
            public void doConfirm(Calendar calendar) {
                notifyTime = calendar.getTime();
                newToDoTime = new TimeUtil(notifyTime).getTimeString();
                toDoNotifyTime.setText("提醒时间："+new TimeUtil(notifyTime).getTimeString());
                Log.d(TAG, "doConfirm: "+new TimeUtil(notifyTime).getTimeString());
                if (notifyTime.getTime()>System.currentTimeMillis()){
                    todoNotify=1;
                }
                timeSelectDialog.dismiss();
            }
            @Override
            public void doCancel() {
                timeSelectDialog.dismiss();
            }
        });
    }

}