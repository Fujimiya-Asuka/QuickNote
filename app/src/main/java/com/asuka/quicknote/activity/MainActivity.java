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
import android.widget.Toast;
import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.ViewPagerAdapter;
import com.asuka.quicknote.utils.NotifyService;
import com.asuka.quicknote.utils.db.NoteCRUD;
import com.asuka.quicknote.domain.Time;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                    startActivity(new Intent(MainActivity.this, NoteEditActivity.class));
                }
                //在待办页
                else if(fragmentId==1){
                    startActivity(new Intent(MainActivity.this, ToDoEditActivity.class));
                }
            }
        });

        //侧滑抽屉NavigationView里Item的点击事件
        NavigationView navigationView = findViewById(R.id.navigation_view_main);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_AAA:
                        Toast.makeText(MainActivity.this, "点击AAA", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers(); //关闭侧滑抽屉
                        break;
                    case R.id.nav_BBB:
                        Toast.makeText(MainActivity.this, "点击BBB", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_CCC:
                        Toast.makeText(MainActivity.this, "注销登录", Toast.LENGTH_SHORT).show();
                        //修改登录信息
                        //利用SharedPreference将登录信息存储到config文件中
                        SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                        editor.putInt("isLogin", 0);
                        editor.apply();
                        finish();
                        startActivity(new Intent(MainActivity.this,LauncherActivity.class));
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
                NoteCRUD noteCRUD = new NoteCRUD(MainActivity.this);
                String title = "听雨";
                String data ="那一刻，我的心情随着雨声沸腾着，一些旧时光里的人或者事，就像雨珠一样飘飞，穿过时光，" +
                        "越过天涯，轻轻地落在心上，溅起一层层涟漪。而在这涟漪的波光里，仿佛一切都生动起来，鲜活起来。若雨，" +
                        "丝丝缠绵，滴滴惊心。真是应了宋代词人蒋捷那句词：“悲欢离合总无情，一任阶前点滴到天明" +
                        "雨是最能渲染情绪的，不同的人听雨，就能听出不一样的感觉。不同的心境，感受也就各异。" +
                        "心情好的人，听出了欢喜，细品出了轻快；忧伤的人，听出了忧愁，浅尝出了失落，寂寞。" +
                        "而纵观古今，很多文人墨客都喜欢借雨抒发情怀，吟唱心灵之歌。";
                String time = new Time(new Date()).getTime();
                noteCRUD.addNote(title,data,time);
                noteCRUD.closeDB();
                onResume();
                break;

            case R.id.deleteTable:
                if(fragmentId==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否要清空所有笔记？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new NoteCRUD(MainActivity.this).removeAllNotes("NOTE");
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
                            new NoteCRUD(MainActivity.this).removeAllNotes("TODO");
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
