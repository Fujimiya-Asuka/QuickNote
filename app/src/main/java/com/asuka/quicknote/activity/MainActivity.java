package com.asuka.quicknote.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.ViewPagerAdapter;
import com.asuka.quicknote.domain.Note;
import com.asuka.quicknote.domain.ResponseSyncToDoMessage;
import com.asuka.quicknote.domain.ToDo;
import com.asuka.quicknote.utils.NetWorkUtil;
import com.asuka.quicknote.utils.NotifyService;
import com.asuka.quicknote.utils.db.DatabaseHelper;
import com.asuka.quicknote.utils.db.NoteCRUD;
import com.asuka.quicknote.utils.db.ToDoCRUD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final Context mContext = MainActivity.this;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabLayoutMediator tabLayoutMediator;
    public int fragmentId=0; //用来记录当前的Fragment
    final List<String> tableName = new ArrayList<String>(){{add("便签");add("待办");}};


    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout_main);
        tabLayout = findViewById(R.id.tabLayout_main);
        viewPager2 = findViewById(R.id.viewPager2_main);

        //toolbar
        toolbar = findViewById(R.id.toolBar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //去掉toolbar的默认显示Title

        //启动待办提醒服务
        startService(new Intent(this, NotifyService.class));


        //主页头像的点击事件 打开侧滑抽屉
        ImageView imageView = findViewById(R.id.circleImageView_main);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        //添加按钮
        FloatingActionButton addBtn = findViewById(R.id.add_btn_main);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在便签页 跳转到笔记编辑界面
                if(fragmentId==0){
                    startActivity(new Intent(mContext, NoteEditActivity.class));
                }
                //在待办页
                else if(fragmentId==1){
                    startActivity(new Intent(mContext, ToDoEditActivity.class));
                }
            }
        });


        NavigationView navigationView = findViewById(R.id.navigation_view_main);
        //headerLayout的用户名TextView
        TextView nv = navigationView.getHeaderView(0).findViewById(R.id.nv_username);
        nv.setText(getSharedPreferences("config",MODE_PRIVATE).getString("user","userName"));
        //侧滑抽屉NavigationView里Item的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_setting:
                        startActivity(new Intent(mContext, SettingActivity.class));
                        drawerLayout.closeDrawers(); //关闭侧滑抽屉
                        break;
                    case R.id.nav_signOut:
                        Toast.makeText(mContext, "注销登录", Toast.LENGTH_SHORT).show();
                        new DatabaseHelper(mContext).deleteDataBase();
                        //修改登录信息
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                        editor.remove("isLogin");
                        editor.remove("note_tableName");
                        editor.remove("todo_tableName");
                        editor.apply();
                        finish();
                        startActivity(new Intent(mContext,LauncherActivity.class));
                        break;
                }
                return true;
            }
        });

        //如果从登录页面启动，默认自动下载数据
        if (getIntent().getIntExtra("login",0)==1){
            downloadNotSyncNote(0);
            downloadNotSyncTodo(0);
        }

    }


    //为toolbar加载菜单xml文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    //toolbar菜单的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.syncBtn :

                if(fragmentId==0){
                    int localNoteMaxModify = new NoteCRUD(mContext).getLocalNoteMaxModify();
                    Log.d(TAG, "localNoteMaxModify："+localNoteMaxModify);
                    downloadNotSyncNote(localNoteMaxModify);
                }else if (fragmentId==1){
//                    List<ToDo> allNotSyncTodo = new ToDoCRUD(mContext).getAllNotSyncTodo();
//                    Log.d(TAG, "需要同步上传的待办数量: "+allNotSyncTodo.size());
//                    //有未同步的待办才开启同步方法
//                    if (allNotSyncTodo.size()>0){
//                        uploadNotSyncTodo(allNotSyncTodo);
//                    }
                    int localToDoMaxModify = new ToDoCRUD(getApplicationContext()).getLocalToDoMaxModify();
                    Log.d(TAG, "localToDoMaxModify："+localToDoMaxModify);
                    downloadNotSyncTodo(localToDoMaxModify);
                }
                break;

            case R.id.deleteBtn_MainTable:
                if(fragmentId==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否要清空所有笔记？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new NoteCRUD(mContext).removeAllNote();
                            onResume();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //什么也不做
                        }
                    });
                    builder.create();
                    builder.show();
                    break;
                }else if(fragmentId==1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否要清空所有待办？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ToDoCRUD(mContext).removeAllTodo();
                            onResume();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //什么也不做
                        }
                    });
                    builder.create();
                    builder.show();
                }
            default:
                break;
        }
        return true;
    }

    /**
     * 上传待办的方法
     * @Author:  XuZhenHui
     * @Time:  2021/4/23 0:57
     * @param notSyncToDoList
     * 未同步的待办列表
     */
    private void uploadNotSyncTodo(final List<ToDo> notSyncToDoList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                String json = new Gson().toJson(notSyncToDoList);
                Log.d(TAG, "syncTodo方法发送的内容"+json);
                RequestBody requestBody = RequestBody.create(mediaType, json);
                Request request = new Request.Builder()
                        .url("http://192.168.31.192:8080/QuickNoteServlet/SyncServlet")
                        .post(requestBody)
                        .addHeader("user", "123")
                        .addHeader("method", "uploadToDo")
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d(TAG, "SyncTodo_onFailure: "+e);
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String string = response.body().string();
                        Log.d(TAG, "onResponse: "+string);
                        ResponseSyncToDoMessage responseSyncToDoMessage = new Gson().fromJson(string, ResponseSyncToDoMessage.class);
                        if (responseSyncToDoMessage!=null){
                            Log.d(TAG, "onResponse: "+responseSyncToDoMessage.toString());
                            int resultCode = responseSyncToDoMessage.getResultCode();
                            switch (resultCode){
                                case 1 :
                                    Log.d(TAG, "switch:");
                                    new ToDoCRUD(getBaseContext()).updateToDoStateModify(
                                            responseSyncToDoMessage.getTodoID(),
                                            2,
                                            responseSyncToDoMessage.getModify());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 下载待办的方法
     * @Author:  XuZhenHui
     * @Time:  2021/4/26 16:12
     * @param modify
     * 本地待办最大时间戳
     */
    private void downloadNotSyncTodo(final int modify){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NetWorkUtil(getApplicationContext()).downloadNotSyncTodo(modify).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //有回写数据才执行
                        if (response.body().contentLength()>0){
                            List<ToDo> toDoList = new Gson().fromJson(response.body().string(), new TypeToken<List<ToDo>>() {}.getType());
                            final int syncItemCount = new ToDoCRUD(mContext).addTodoToLocal(toDoList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onResume();
                                    Toast.makeText(mContext, "成功获取了"+syncItemCount+"条数据", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "本地数据已经是最新啦", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 下载待办的方法
     * @Author:  XuZhenHui
     * @Time:  2021/4/26 16:12
     * @param modify
     * 本地待办最大时间戳
     */
    private void downloadNotSyncNote(final int modify){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NetWorkUtil(getApplicationContext()).downloadNotSyncNote(modify).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //有回写数据才执行
                        if (response.body().contentLength()>0){
                            List<Note> noteList = new Gson().fromJson(response.body().string(), new TypeToken<List<Note>>() {}.getType());
                            final int syncItemCount = new NoteCRUD(getApplicationContext()).addNoteToLocal(noteList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onResume();
                                    Toast.makeText(mContext, "成功获取了"+syncItemCount+"条数据", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "本地数据已经是最新啦", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        //初始化ViewPager2
        ViewPagerAdapter mainActivityViewPagerAdapter = new ViewPagerAdapter(this,tableName);
        viewPager2.setAdapter(mainActivityViewPagerAdapter);


        //判断viewPager是否默认加载ToDoFragment
        if(fragmentId ==1){
            //smoothScroll 是否需要平滑滑动，好像设置页面跳过超过10个的时候会有Bug。
            viewPager2.setCurrentItem(1,false);
        }

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0){
                    tab.setText(tableName.get(0));
                }else {
                    tab.setText(tableName.get(1));
                }
            }
        });
        tabLayoutMediator.attach();



    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new DatabaseHelper(mContext).close();
        Log.d(TAG, "onDestroy");
    }

    //如果当前Activity是SingleTask模式，想要获得传过来的Intent的值必须要重写onNewIntent方法
    //调用setIntent(intent)方法
    //并在其中获取想要的值，否则会无法正确获取
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


}
