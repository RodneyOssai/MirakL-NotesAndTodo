package com.figurehowto.mirakl;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class TodoAdapter extends ListAdapter<Todo, TodoAdapter.ViewHolder> {

    private List<Todo> todos;
    private onTodoCompletedListener mOnTodoCompletedListener;
    private onItemClickListener listener;

    public TodoAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Todo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Todo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getTask_todo().equals(newItem.getTask_todo());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View todoView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(todoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView todoText = holder.todo_item;

        todoText.setText(getItem(position).getTask_todo());
        if(getItem(position).getCompleted()){
            todoText.setPaintFlags(todoText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.completed.setChecked(true);
        }else{
            todoText.setPaintFlags(todoText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.completed.setChecked(false);
        }
    }

    public Todo getTodoAt(int position) {
        return getItem(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView todo_item, datetime;
        CheckBox completed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            todo_item = itemView.findViewById(R.id.list_item);
            //datetime = itemView.findViewById(R.id.todoDateTime);
            completed = itemView.findViewById(R.id.todoCompleted);
            // Define click listener for the ViewHolder's View
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(getItem(position));
                    }
                }
            });
            // Define click listener for the Checkbox
            completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (mOnTodoCompletedListener != null && position != RecyclerView.NO_POSITION) {
                        mOnTodoCompletedListener.todoCompleted(getItem(position),isChecked);
                    }

                }
            });
        }

    }

    public interface onTodoCompletedListener {

        void todoCompleted(Todo todo,boolean isChecked);

    }

    public interface onItemClickListener {
        void OnItemClick(Todo todo);
    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
    public void setOnTodoCompletedListener( onTodoCompletedListener mOnTodoCompletedListener) {
        this.mOnTodoCompletedListener = mOnTodoCompletedListener;
    }

}
