package com.figurehowto.mirakl;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TodoDao {

    @Insert
    void addTodo(Todo todo);

    @Update
    void updateTodo(Todo todo);

    @Delete
    void deleteTodo(Todo todo);

    @Query("DELETE FROM todos_table")
    void deleteAllTodos();

    @Query("DELETE FROM todos_table WHERE noteId = :noteId")
    void deleteTodosByNoteId(String noteId);

    @Query("SELECT * FROM todos_table")
    LiveData<List<Todo>> getAllTodos();

    @Query("SELECT * FROM todos_table WHERE noteId = :noteId ORDER BY createdAt ASC,completed")
    LiveData<List<Todo>> getTodosByNoteId(String noteId);

    @Query("UPDATE todos_table SET completed = 1 WHERE id = :id")
    void setCompleted(String id);

    @Query("UPDATE todos_table SET completed = 0 WHERE id = :id")
    void setUncompleted(String id);

    @Query("UPDATE todos_table SET completed = 10 WHERE id = :id")
    void setLate(String id);

}
