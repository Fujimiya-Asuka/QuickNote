package com.example.quicknote;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter <NoteAdapter.ViewHolder>{

    private List<Note> mNoteList;


    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);
                Intent intent = new Intent(parent.getContext(),EditActivity.class);
                parent.getContext().startActivity(intent);
            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        holder.tv_note_title.setText(note.getTitle());
        holder.tv_note_data.setText(note.getData());
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
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

    public NoteAdapter(List<Note> NoteList) {
        mNoteList = NoteList;
    }
}
