package com.asuka.quicknote.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asuka.quicknote.utils.db.NoteCRUD;
import com.asuka.quicknote.domain.Note;
import com.asuka.quicknote.activity.NoteViewActivity;
import com.asuka.quicknote.R;
import com.daimajia.swipe.SwipeLayout;
import java.util.ArrayList;
import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter <NoteRecyclerViewAdapter.ViewHolder>{
    private final String TAG = "NoteRecyclerViewAdapter";
    private boolean isUesCardView;
    NoteCRUD noteCRUD;
    List<Note> noteList = new ArrayList<>(50);

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void setUesCardView(boolean uesCardView) {
        isUesCardView = uesCardView;
    }

    @NonNull
    @Override
    public NoteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: NoteRecyclerViewAdapter");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (isUesCardView==true){
            view = layoutInflater.inflate(R.layout.note_item_card,parent,false);
        }else {
            view = layoutInflater.inflate(R.layout.note_item_list,parent,false);
        }

        final ViewHolder holder = new ViewHolder(view);
        noteCRUD = new NoteCRUD(parent.getContext());
        //添加右滑动
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,holder.swipeLayout.findViewById(R.id.bottom_wrapper));
        //关闭向右滑动
        holder.swipeLayout.setRightSwipeEnabled(false);

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
        holder.deleteThisNote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = noteList.get(position);
                long id = note.getId();
                NoteCRUD noteCRUD = new NoteCRUD(v.getContext());
                noteCRUD.deleteNote(id);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(v.getContext());
                Intent intent = new Intent("com.asuka.quicknote.activity.DELETE_THIS_NOTE");
                localBroadcastManager.sendBroadcast(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteRecyclerViewAdapter.ViewHolder holder,int position) {
        Log.d(TAG, "onBindViewHolder: NoteRecyclerViewAdapter");
        final Note note = noteList.get(position);
        holder.tv_note_title.setText(note.getTitle());
        holder.tv_note_data.setText(note.getData());
        holder.tv_note_time.setText(note.getTime());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_note_title,tv_note_data,tv_note_time;
        private View noteView;
        private SwipeLayout swipeLayout;
        private Button deleteThisNote_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipeLayout_note);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            noteView = itemView.findViewById(R.id.note_view);
            //标题
            tv_note_title = itemView.findViewById(R.id.note_title);
            //内容
            tv_note_data = itemView.findViewById(R.id.note_data);
            //时间
            tv_note_time = itemView.findViewById(R.id.note_time);
            //滑动删按钮
            deleteThisNote_btn = swipeLayout.findViewById(R.id.deleteThisNote_btn);

        }
    }


}
