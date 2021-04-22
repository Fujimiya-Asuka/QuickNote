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
import android.database.sqlite.SQLiteDatabase;
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
import com.asuka.quicknote.domain.ResponseSyncToDoMessage;
import com.asuka.quicknote.domain.ToDo;
import com.asuka.quicknote.utils.NotifyService;
import com.asuka.quicknote.utils.db.DatabaseHelper;
import com.asuka.quicknote.utils.db.NoteCRUD;
import com.asuka.quicknote.utils.TimeUtil;
import com.asuka.quicknote.utils.db.ToDoCRUD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
                    case R.id.nav_AAA:
                        Toast.makeText(mContext, "点击AAA", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers(); //关闭侧滑抽屉
                        break;
                    case R.id.nav_BBB:
                        Toast.makeText(mContext, "点击BBB", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_CCC:
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
            case R.id.AAA:
                List<ToDo> allNotSyncTodo = new ToDoCRUD(mContext).getAllNotSyncTodo();
                Log.d(TAG, "allNotSyncTodo.size: "+allNotSyncTodo.size());
                //有未同步的待办才开启同步
                if (allNotSyncTodo.size()>0){
                    syncTodo(allNotSyncTodo);
                }
//                                    new ToDoCRUD(mContext).updateToDoStateModify(
//                                            3,
//                                            2,
//                                            1);
                break;

            case R.id.deleteTable:
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

    private void syncTodo(final List<ToDo> toDoList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                String s = new Gson().toJson(toDoList);
                Log.d(TAG, "run: "+s);
                RequestBody requestBody = RequestBody.create(mediaType, s);
                Request request = new Request.Builder()
                        .url("http://192.168.137.1:8080/QuickNoteServlet/SyncServlet")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

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

//    //如果当前Activity是SingleTask模式，想要获得传过来的Intent的值必须要重写onNewIntent方法
//    //调用setIntent(intent)方法
//    //并在其中获取想要的值，否则会无法正确获取
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//    }
}
