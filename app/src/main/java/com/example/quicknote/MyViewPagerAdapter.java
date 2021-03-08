package com.example.quicknote;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//ViewPager适配器
public class MyViewPagerAdapter extends RecyclerView.Adapter<MyViewPagerAdapter.ViewPagerHolder> {

    private ArrayList<String> tabName;

    //数据初始化
    public MyViewPagerAdapter(ArrayList<String> tabName) {
        this.tabName=tabName;
    }


    //绑定样式
    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager1,parent,false));
    }



    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        holder.textView.setText(tabName.get(position));
    }


    //设置可以滚动的页数
    @Override
    public int getItemCount() {
        return tabName.size();
    }

    //声明控件
    class ViewPagerHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ConstraintLayout mContent;
        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.viewpager2);
            textView = itemView.findViewById(R.id.textView4);
        }
    }


}
