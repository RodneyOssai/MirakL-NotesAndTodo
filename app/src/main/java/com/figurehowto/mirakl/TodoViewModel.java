package com.figurehowto.mirakl;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TodoViewModel extends AndroidViewModel {

     TodoRepository todoRepository;
     LiveData<List<Todo>> todos;

    public TodoViewModel(@NonNull Application application) {
        super(application);

        todoRepository = new TodoRepository(application);
        todos = todoRepository.getAllTodos();
    }

    public void addTodo(Todo todo) {
        todoRepository.addTodo(todo);
    }

    public void updateTodo(Todo todo) {
        todoRepository.updateTodo(todo);
    }

    public void deleteTodo(Todo todo) {
        todoRepository.deleteTodo(todo);
    }
    public void deleteTodoByNoteId(String noteId) {
        todoRepository.deleteTodoByNoteId(noteId);
    }

    public void deleteAllTodos() {
        todoRepository.deleteAllTodos();
    }

    public void setCompletedTodo(String id) {
        todoRepository.setCompleted(id);
    }

    public void setUncompletedTodo(String id) {
        todoRepository.setUncompleted(id);
    }

    public void setLateTodo(String id) {
        todoRepository.setLate(id);
    }

    public LiveData<List<Todo>> getAllTodos() {
        return todos;
    }

    public LiveData<List<Todo>> getTodosByNoteId(String noteID) {
        Log.d("TODOS", "getTodosByCategory: " + noteID);
        return this.todoRepository.getTodosByNoteId(noteID);
    }


}
