package com.asuka.quicknote.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asuka.quicknote.myClass.Note;
import com.asuka.quicknote.activity.NoteViewActivity;
import com.asuka.quicknote.R;

import java.util.ArrayList;
import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter <NoteRecyclerViewAdapter.ViewHolder>{

    List<Note> noteList = new ArrayList<>();

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = noteList.get(position);
                long id = note.getId();
                Intent intent = new Intent(parent.getContext(), NoteViewActivity.class);
                intent.putExtra("Note_id",id); //向下一个活动传递noteID
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerViewAdapter.ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tv_note_title.setText(note.getTitle());
        holder.tv_note_data.setText(note.getData());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_note_title,tv_note_data;
        private View noteView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteView = itemView;
            tv_note_title = itemView.findViewById(R.id.note_title);
            tv_note_data = itemView.findViewById(R.id.note_data);
        }
    }



}
