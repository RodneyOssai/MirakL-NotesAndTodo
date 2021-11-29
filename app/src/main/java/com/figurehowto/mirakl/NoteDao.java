package com.figurehowto.mirakl;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

//This class is our Data Access Object, here we define all the database operations we want to make on the Note Entity(SQLliteDB)
@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete (Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table ORDER BY lastModified DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT type FROM note_table WHERE id = :id")
    int getNoteType(int id);
}
