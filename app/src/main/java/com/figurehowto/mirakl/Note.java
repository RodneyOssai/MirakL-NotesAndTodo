package com.figurehowto.mirakl;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
//This is our Note class annotated with entity so room creates an SQLlite DB Table for it
@Entity(tableName = "note_table")
public class Note {
    //These are the DB columns for Table = note_table
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String note_title;
    private String note_body;
    private int type;
    private String noteID;
    long createdAt;
    long lastModified;
    long alarmTime;

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Note(String note_title, String note_body, int type, long lastModified) {
        this.note_title = note_title;
        this.note_body = note_body;
        this.type = type;
        this.lastModified = lastModified;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNote_title() {
        return note_title;
    }

    public String getNote_body() {
        return note_body;
    }

    public int getType() {
        return type;
    }
}
