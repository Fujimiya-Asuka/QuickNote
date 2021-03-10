package com.asuka.quicknote.activity;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.asuka.quicknote.R;
import com.asuka.quicknote.adapter.LauncherActivityViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
public class LauncherActivity extends FragmentActivity {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        viewPager2 = findViewById(R.id.viewpager2);
        tabLayout = findViewById(R.id.tablayout);

        //提取登录信息
        SharedPreferences preferences =getSharedPreferences("config", Context.MODE_PRIVATE);
        int isLogin = preferences.getInt("isLogin",0);
        if(isLogin==1){
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            /*初始化viewPager2*/
            final ArrayList<String> tapName = new ArrayList<>();
            tapName.add("登录");
            tapName.add("注册");
            LauncherActivityViewPagerAdapter viewPagerAdapter = new LauncherActivityViewPagerAdapter(this,tapName);
            viewPager2.setAdapter(viewPagerAdapter);

            /*设置tableLayout标签，与viewPager2进行绑定联动*/
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    if (position == 0) {
                        tab.setText(tapName.get(0));
                    } else {
                        tab.setText(tapName.get(1));
                    }
                }
            });
            tabLayoutMediator.attach();

        }


    }


}