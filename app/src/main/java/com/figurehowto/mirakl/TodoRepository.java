package com.figurehowto.mirakl;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

// The repository uses NoteDao(Data access Object to retrieve DB the entries from our notes table)
public class TodoRepository {

    private TodoDao todoDao;
    private LiveData<List<Todo>> allTodos;


    public TodoRepository(Application application) {
        //We get create an instance of the database in the repository
        TodoDatabase todoDatabase = TodoDatabase.getInstance(application);
        //From the database instance we call the todoDao() method in NoteDatabase
        todoDao = todoDatabase.todoDao();
        //Using the TodoDao getAllNotes method we retrieve all todos from the DB
        allTodos = todoDao.getAllTodos();
    }

    /* Here we create methods for all our different database operations*/
    public void addTodo(Todo todo) {
        new AddTodoAsync(todoDao).execute(todo);
    }

    public void updateTodo(Todo todo) {
        new UpdateTodoAsync(todoDao).execute(todo);
    }

    public void deleteTodo(Todo todo) {
        new DeleteTodoAsync(todoDao).execute(todo);
    }
    public void deleteTodoByNoteId(String noteId) {
        new DeleteTodoByIdAsync(todoDao).execute(noteId);
    }

    public void deleteAllTodos() {
        new DeleteAllTodosAsync(todoDao).execute();
    }

    public void setCompleted(String id) {
        new SetTodoCompletedAsync(todoDao).execute(id);
    }

    public void setUncompleted(String id) {
        new SetTodoUncompletedAsync(todoDao).execute(id);
    }

    public void setLate(String id) {
        new SetTodoLateAsync(todoDao).execute(id);
    }
    public LiveData<List<Todo>> getTodosByNoteId(String noteId) {

        LiveData<List<Todo>> todos = null;

        try {
            GetTodosByNoteIDAsync getTodosByNoteId = new GetTodosByNoteIDAsync(todoDao);
            todos = getTodosByNoteId.execute(noteId).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return todos;

    }


    public LiveData<List<Todo>> getAllTodos() {
        return allTodos;
    }


    public static class AddTodoAsync extends AsyncTask<Todo, Void, Void> {

        private TodoDao todoDao;

        public AddTodoAsync(TodoDao todoDao) {
            this.todoDao = todoDao;
        }

        @Override
        protected Void doInBackground(@org.jetbrains.annotations.NotNull Todo... todos) {
            todoDao.addTodo(todos[0]);
            return null;
        }
    }

    public static class UpdateTodoAsync extends AsyncTask<Todo, Void, Void> {

        TodoDao dao;

        public UpdateTodoAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            this.dao.updateTodo(todos[0]);
            return null;
        }
    }

    public static class DeleteTodoAsync extends AsyncTask<Todo, Void, Void> {

        TodoDao dao;

        public DeleteTodoAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            this.dao.deleteTodo(todos[0]);
            return null;
        }
    }
    public static class DeleteTodoByIdAsync extends AsyncTask<String, Void, Void> {

        TodoDao dao;

        public DeleteTodoByIdAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... todos) {
            this.dao.deleteTodosByNoteId(todos[0]);
            return null;
        }
    }

    public static class DeleteAllTodosAsync extends AsyncTask<Void, Void, Void> {

        TodoDao dao;

        public DeleteAllTodosAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.dao.deleteAllTodos();
            return null;
        }
    }

    public static class SetTodoCompletedAsync extends AsyncTask<String, Void, Void> {

        TodoDao dao;

        public SetTodoCompletedAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            this.dao.setCompleted(strings[0]);
            return null;
        }
    }

    public static class SetTodoUncompletedAsync extends AsyncTask<String, Void, Void> {

        TodoDao dao;

        public SetTodoUncompletedAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            this.dao.setUncompleted(strings[0]);
            return null;
        }
    }


    public static class SetTodoLateAsync extends AsyncTask<String, Void, Void> {

        TodoDao dao;

        public SetTodoLateAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            this.dao.setLate(strings[0]);
            return null;
        }
    }

    public static class GetTodosByNoteIDAsync extends AsyncTask<String, Void, LiveData<List<Todo>>> {

        TodoDao dao;

        public GetTodosByNoteIDAsync(TodoDao dao) {
            this.dao = dao;
        }

        @Override
        protected LiveData<List<Todo>> doInBackground(String... strings) {
            return this.dao.getTodosByNoteId(strings[0]);
        }
    }

}
