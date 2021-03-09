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
        SharedPreferences preferences =getSharedPreferences("config", Context.MODE_PRIVATE);
        int isLogin = preferences.getInt("isLogin",0);//提取是否已登录信息
        if(isLogin==1){
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            final ArrayList<String> tapName = new ArrayList<>();
            tapName.add("登录");
            tapName.add("注册");
            LauncherActivityViewPagerAdapter viewPagerAdapter = new LauncherActivityViewPagerAdapter(this,tapName);
            viewPager2.setAdapter(viewPagerAdapter);
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    switch (position){
                        case 0 :
                            tab.setText(tapName.get(0));
                            break;
                        default:
                            tab.setText(tapName.get(1));
                    }
                }
            });
            tabLayoutMediator.attach();

        }


    }


}