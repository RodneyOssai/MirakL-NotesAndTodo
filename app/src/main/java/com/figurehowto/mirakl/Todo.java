package com.figurehowto.mirakl;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//This is our Todos class annotated with entity so room creates an SQLlite DB Table for it
@Entity(tableName = "todos_table")
public class Todo {
    //These are the DB columns for Table = todos_table
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String task_todo;
    private long createdAt;
    public boolean completed;
    public String noteId;



    public Todo(String task_todo, long createdAt, String noteId) {
        this.task_todo = task_todo;
        this.createdAt = createdAt;
        this.noteId = noteId;
    }
    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getCompleted() {
        return completed;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_todo() {
        return task_todo;
    }

    public void setTask_todo(String task_todo) {
        this.task_todo = task_todo;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
