package com.asuka.quicknote.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.asuka.quicknote.R;
import com.asuka.quicknote.activity.MainActivity;
import com.asuka.quicknote.adapter.ToDoRecycleViewAdapter;
import com.asuka.quicknote.utils.db.ToDoCRUD;
import com.asuka.quicknote.domain.ToDo;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToDoMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = "FragmentB: ";

    private DeleteThisToDoReceiver deleteThisToDoReceiver;
    private FragmentActivity fragmentActivity;
    private RecyclerView recyclerView;
    private ToDoRecycleViewAdapter toDoRecycleViewAdapter;
    private SearchView searchView;
    private View syncBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToDoMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDoMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoMainFragment newInstance(String param1, String param2) {
        ToDoMainFragment fragment = new ToDoMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,TAG+"onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,TAG+"onCreateView");
        return inflater.inflate(R.layout.fragment_to_do_main, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,TAG+"onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        fragmentActivity = requireActivity();
        recyclerView = fragmentActivity.findViewById(R.id.todoRecycleView_main);
        searchView = fragmentActivity.findViewById(R.id.searchView_main);
//        syncBtn = fragmentActivity.findViewById(R.id.syncBtn);

        //展示RecycleView数据
        toDoRecycleViewAdapter = new ToDoRecycleViewAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(toDoRecycleViewAdapter);
        ToDoCRUD toDoCRUD = new ToDoCRUD(this.fragmentActivity);
        toDoRecycleViewAdapter.setTodoList(toDoCRUD.getAllTodo());

        //注册本地广播接收器(删除待办)
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(fragmentActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.asuka.quicknote.activity.DELETE_THIS_TODO");
        deleteThisToDoReceiver = new ToDoMainFragment.DeleteThisToDoReceiver();
        localBroadcastManager.registerReceiver(deleteThisToDoReceiver,intentFilter);
    }

    @Override
    public void onResume() {
        Log.d(TAG,TAG+"onResume"+fragmentActivity);
        super.onResume();
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setFragmentId(1);//记录当前Fragment返回给Activity

        //搜索框
        searchView.setQueryHint("搜索待办");//设置搜索提示
        searchView.setBackgroundColor(Color.TRANSPARENT);//设置下划线透明
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<ToDo> toDoList = new ToDoCRUD(fragmentActivity).searchTodo(newText.trim());
                toDoRecycleViewAdapter.setTodoList(toDoList);
                toDoRecycleViewAdapter.notifyDataSetChanged();
                if (toDoList.size() == 0) {
                    Toast.makeText(fragmentActivity, "没有数据啦，不要再找了", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    //广播接收器，用来接收删除待办的广播，刷新RecycleView
    class DeleteThisToDoReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ToDo> allTodo = new ToDoCRUD(context).getAllTodo();
            toDoRecycleViewAdapter.setTodoList(allTodo);
            toDoRecycleViewAdapter.notifyDataSetChanged();
        }
    }



}