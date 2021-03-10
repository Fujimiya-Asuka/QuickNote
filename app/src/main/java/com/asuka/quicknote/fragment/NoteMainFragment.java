package com.asuka.quicknote.fragment;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import com.asuka.quicknote.R;
import com.asuka.quicknote.activity.MainActivity;
import com.asuka.quicknote.adapter.NoteRecyclerViewAdapter;
import com.asuka.quicknote.db.NoteCRUD;
import com.asuka.quicknote.myClass.Note;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = "FragmentA";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentActivity fragmentActivity;
    private RecyclerView recyclerView;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private SearchView searchView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoteMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteMainFragment newInstance(String param1, String param2) {
        NoteMainFragment fragment = new NoteMainFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,TAG+"onCreateView");
        return inflater.inflate(R.layout.fragment_note_main_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,TAG+"onActivityCreated");
        fragmentActivity = requireActivity();
//        requireActivity()调用的就是getActivity(),返回与此Fragment绑定的Activity，但是避免了返回null？
//        requireContext() getContext() 同理？

        recyclerView = fragmentActivity.findViewById(R.id.noteRecycleView_main);
        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteRecyclerViewAdapter);
        searchView = fragmentActivity.findViewById(R.id.searchView_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,TAG+"onResume");

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setFragmentId(1);//记录当前Fragment返回给Activity

        NoteCRUD noteCRUD = new NoteCRUD(this.fragmentActivity);
        List<Note> allNotes = noteCRUD.getAllNotes();
        noteRecyclerViewAdapter.setNoteList(allNotes);
        noteRecyclerViewAdapter.notifyDataSetChanged(); //告诉适配器数据已经发生变化

        //搜索框
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                NoteCRUD noteCRUD = new NoteCRUD(NoteMainFragment.this.fragmentActivity);
                List<Note> notes = noteCRUD.searchNotes(newText);
                noteRecyclerViewAdapter.setNoteList(notes);
                noteRecyclerViewAdapter.notifyDataSetChanged();
                if (notes.size() == 0) {
                    Toast.makeText(NoteMainFragment.this.fragmentActivity, "没有数据啦，不要再找了", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }




}