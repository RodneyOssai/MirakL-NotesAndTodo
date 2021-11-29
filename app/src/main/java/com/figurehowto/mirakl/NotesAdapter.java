package com.figurehowto.mirakl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends ListAdapter<Note,NotesAdapter.ViewHolder> {
    private onItemClickListener listener;

    protected NotesAdapter() {
        super(DIFF_CALLBACK);
    }
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getNote_title().equals(newItem.getNote_title()) &&
                    oldItem.getNote_body().equals(newItem.getNote_body());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.noteTitle.setText(getItem(position).getNote_title());
        holder.noteBody.setText(getItem(position).getNote_body());
        if(getItem(position).getAlarmTime() != 0L){
            holder.isAlarmSet.setVisibility(View.VISIBLE);
        }
    }


    public Note getNoteAt(int position) {
        return getItem(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView noteTitle, noteBody;
        ImageView isAlarmSet;

        public ViewHolder(View view) {
            super(view);
            noteTitle = (TextView) view.findViewById(R.id.noteTitle);
            noteBody = (TextView) view.findViewById(R.id.noteBody);
            isAlarmSet=(ImageView)view.findViewById(R.id.alarm_set);

            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(getItem(position));
                    }

                }
            });
        }

    }

    public interface onItemClickListener {
        void OnItemClick(Note note);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
