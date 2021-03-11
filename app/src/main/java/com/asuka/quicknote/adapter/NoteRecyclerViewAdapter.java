package com.asuka.quicknote.adapter;

import android.content.ComponentName;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asuka.quicknote.db.NoteCRUD;
import com.asuka.quicknote.myClass.Note;
import com.asuka.quicknote.activity.NoteViewActivity;
import com.asuka.quicknote.R;
import com.daimajia.swipe.SwipeLayout;
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
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_test,parent,false);
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

        //滑动删除，删除数据并发送广播通知刷新UI
        holder.deleteThisNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = noteList.get(position);
                long id = note.getId();
                NoteCRUD noteCRUD = new NoteCRUD(v.getContext());
                noteCRUD.removeNote(id);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(v.getContext());
                Intent intent = new Intent("com.asuka.quicknote.activity.DELETE_THIS_NOTE");
                localBroadcastManager.sendBroadcast(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteRecyclerViewAdapter.ViewHolder holder,int position) {
        final Note note = noteList.get(position);
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
        private SwipeLayout swipeLayout;
        private Button deleteThisNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteView = itemView;
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            noteView = itemView.findViewById(R.id.note_view);
            tv_note_title = itemView.findViewById(R.id.note_title1);
            tv_note_data = itemView.findViewById(R.id.note_data1);
            deleteThisNote = itemView.findViewById(R.id.deleteThisNote_main);

        }
    }


}
