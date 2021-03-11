package com.asuka.quicknote.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.MainActivityViewPagerAdapter;
import com.asuka.quicknote.adapter.NoteRecyclerViewAdapter;
import com.asuka.quicknote.db.NoteCRUD;
import com.asuka.quicknote.fragment.NoteMainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabLayoutMediator tabLayoutMediator;
    public int fragmentId=0; //用来记录当前的Fragment
    final List<String> tableName = new ArrayList<String>(){{add("笔记");add("待办");}};


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


        //主页头像的点击事件 打开侧滑抽屉
        ImageView imageView = findViewById(R.id.circleImageView_main);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        //添加笔记按钮 跳转到笔记编辑界面
        FloatingActionButton addBtn = findViewById(R.id.add_btn_main);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
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
                        Toast.makeText(MainActivity.this, "点击CCC", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }


    //为toolbar加载菜单xml文件
    @SuppressLint("ResourceType")
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

                break;
            case R.id.dropTable:
                if(fragmentId==1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否要清空所有笔记？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new NoteCRUD(MainActivity.this).removeAllNotes("NOTE");
                            NoteMainFragment noteMainFragment = (NoteMainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_note_main);
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
                }
            default:
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
        MainActivityViewPagerAdapter mainActivityViewPagerAdapter = new MainActivityViewPagerAdapter(this,tableName);
        viewPager2.setAdapter(mainActivityViewPagerAdapter);
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

}
