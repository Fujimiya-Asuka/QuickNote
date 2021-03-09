package com.asuka.quicknote.activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;


import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.NoteRecyclerViewAdapter;
import com.asuka.quicknote.myClass.Note;
import com.asuka.quicknote.db.NoteCRUD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NoteRecyclerViewAdapter adapter = new NoteRecyclerViewAdapter();
    private DrawerLayout drawerLayout;
    private SearchView searchView;
    private NoteCRUD noteCRUD = new NoteCRUD(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout_main);


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

        //初始化recyclerView
        recyclerView = findViewById(R.id.note_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //加载数据到适配器
        List<Note> noteList = noteCRUD.getAllNotes();
        adapter.setNoteList(noteList);
        //显示数据
        recyclerView.setAdapter(adapter);


        //添加笔记按钮 跳转到笔记编辑界面
        FloatingActionButton addBtn = findViewById(R.id.add_btn_main);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                finish();

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

        searchView = findViewById(R.id.searchView_main);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //该方法点击时执行
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, query);

                return false;
            }

            //改方法内容改变时执行
            @Override
            public boolean onQueryTextChange(String newText) {
//                Log.d(TAG, newText);
                NoteCRUD noteCRUD = new NoteCRUD(MainActivity.this);
                List<Note> notes = noteCRUD.searchNotes(newText);
                adapter.setNoteList(notes);
                adapter.notifyDataSetChanged();
                if (notes.size() == 0) {
                    Toast.makeText(MainActivity.this, "没有数据啦，不要再找了", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        NoteCRUD noteCRUD = new NoteCRUD(this);
//        List<Note> noteList = noteCRUD.getAllNotes();
//        NoteRecyclerViewAdapter adapter = new NoteRecyclerViewAdapter(noteList);
//        recyclerView.setAdapter(adapter);
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
                Toast.makeText(MainActivity.this, "点击AAA", Toast.LENGTH_SHORT).show();
                break;
            case R.id.BBB:
                Toast.makeText(MainActivity.this, "点击BBB", Toast.LENGTH_SHORT).show();
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
