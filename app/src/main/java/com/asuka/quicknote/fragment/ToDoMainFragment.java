package com.asuka.quicknote.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.asuka.quicknote.R;
import com.asuka.quicknote.activity.MainActivity;
import com.asuka.quicknote.adapter.ToDoRecycleViewAdapter;
import com.asuka.quicknote.db.ToDoCRUD;
import com.asuka.quicknote.myClass.ToDo;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToDoMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoMainFragment extends Fragment {

    private final String TAG = "FragmentB: ";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentActivity fragmentActivity;
    private RecyclerView recyclerView;
    private ToDoRecycleViewAdapter toDoRecycleViewAdapter;
    private SearchView searchView;
    private List<ToDo> allToDo;

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
        toDoRecycleViewAdapter = new ToDoRecycleViewAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(toDoRecycleViewAdapter);
        ToDoCRUD toDoCRUD = new ToDoCRUD(this.fragmentActivity);
        allToDo = toDoCRUD.getAllTodo();
        toDoRecycleViewAdapter.setTodoList(allToDo);
    }

    @Override
    public void onResume() {
        Log.d(TAG,TAG+"onResume");
        super.onResume();
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setFragmentId(1);//记录当前Fragment返回给Activity
    }


}