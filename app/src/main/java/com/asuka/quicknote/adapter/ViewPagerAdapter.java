package com.asuka.quicknote.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.asuka.quicknote.fragment.NoteMainFragment;
import com.asuka.quicknote.fragment.ToDoMainFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<String> tabNameList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tableNameList) {
        super(fragmentActivity);
        this.tabNameList = tableNameList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position==0){
            return new NoteMainFragment();
        }
        return new ToDoMainFragment();
    }

    @Override
    public int getItemCount() {
        return tabNameList.size();
    }
}
