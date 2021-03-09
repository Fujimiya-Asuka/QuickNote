package com.asuka.quicknote.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.asuka.quicknote.fragment.RegisterFragment;
import com.asuka.quicknote.fragment.SignInFragment;

import java.util.ArrayList;

public class LauncherActivityViewPagerAdapter extends FragmentStateAdapter {

    ArrayList<String> tableNameList; //ViewPager的标签列表

    public LauncherActivityViewPagerAdapter(FragmentActivity fragmentActivity, ArrayList<String> tableNameList) {
        super(fragmentActivity);
        this.tableNameList = tableNameList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0 : return new SignInFragment();
            default: return new RegisterFragment();
        }
    }

    //返回切换页面的数量
    @Override
    public int getItemCount() {
        return tableNameList.size();
    }
}
