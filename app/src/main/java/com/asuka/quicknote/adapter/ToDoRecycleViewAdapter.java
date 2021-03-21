package com.asuka.quicknote.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.asuka.quicknote.R;
import com.asuka.quicknote.activity.ToDoEditActivity;
import com.asuka.quicknote.db.ToDoCRUD;
import com.asuka.quicknote.myClass.ToDo;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

public class ToDoRecycleViewAdapter extends RecyclerView.Adapter <ToDoRecycleViewAdapter.ViewHolder>{
    private final String TAG = "ToDoRecycleViewAdapter";
    ToDoCRUD todoCRUD;
    List<ToDo> todoList = new ArrayList<>();

    public void setTodoList(List<ToDo> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ToDoRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: NoteRecyclerViewAdapter");
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_list,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        todoCRUD = new ToDoCRUD(parent.getContext());

//        点击选中的item
        holder.todoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ToDo todo = todoList.get(position);
                long id = todo.getId();
                Intent intent = new Intent(parent.getContext(), ToDoEditActivity.class);
                intent.putExtra("todoID",id); //向下一个活动传递todoID
                Log.d(TAG, "onClick: "+id);
                parent.getContext().startActivity(intent);
            }
        });

        holder.toDoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.todo_title.setPaintFlags(holder.todo_title.getPaintFlags() | (Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.todo_title.setTextColor(buttonView.getResources().getColor(R.color.hitTextColor));
                    int position = holder.getAdapterPosition();
                    ToDo todo = todoList.get(position);
                    long id = todo.getId();
                    todoCRUD.setToDoIsDone(id,1);
                }else {
                    holder.todo_title.setPaintFlags(holder.todo_title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.todo_title.setTextColor(buttonView.getResources().getColor(R.color.blackTextColor));
                    int position = holder.getAdapterPosition();
                    ToDo todo = todoList.get(position);
                    long id = todo.getId();
                    todoCRUD.setToDoIsDone(id,0);
                }
            }
        });

        //滑动删除，删除数据并发送广播通知刷新UI
        holder.deleteThisTodo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ToDo todo = todoList.get(position);
                long id = todo.getId();
                ToDoCRUD toDoCRUD = new ToDoCRUD(v.getContext());
                toDoCRUD.removeTodo(id);
//                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(v.getContext());
//                Intent intent = new Intent("com.asuka.quicknote.activity.DELETE_THIS_NOTE");
//                localBroadcastManager.sendBroadcast(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoRecycleViewAdapter.ViewHolder holder,int position) {
        Log.d(TAG, "onBindViewHolder: NoteRecyclerViewAdapter");
        final ToDo todo = todoList.get(position);
        holder.todo_title.setText(todo.getTitle());
        holder.todo_time.setText(todo.getTime());
        int isDone = todo.getIsDone();
        //如果该待办是已经完成的待办
        if (isDone==1){
            holder.toDoCheckBox.setChecked(true);
            holder.todo_title.setTextColor(holder.todo_title.getResources().getColor(R.color.hitTextColor));
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View todoView;
        private TextView todo_title,todo_time;
        private SwipeLayout swipeLayout;
        private final Button deleteThisTodo_btn;
        private final CheckBox toDoCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            todoView = itemView.findViewById(R.id.todo_view);
            swipeLayout = itemView.findViewById(R.id.swipeLayout_todo);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //标题
            todo_title = itemView.findViewById(R.id.todo_title);
            //时间
            todo_time = itemView.findViewById(R.id.todo_time);
            //滑动删按钮
            deleteThisTodo_btn = itemView.findViewById(R.id.deleteThisTodo_btn);
            //复选框
            toDoCheckBox = itemView.findViewById(R.id.todo_checkbox);

        }
    }


}
