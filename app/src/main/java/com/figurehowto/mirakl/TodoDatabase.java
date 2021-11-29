package com.figurehowto.mirakl;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {
    //Singleton, we cannot create multiple instances on this db
    public static TodoDatabase instance;
    public abstract TodoDao todoDao();

    //synchronized means that only one thread at a time can access this method
    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TodoDatabase.class,
                    "todos_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(dbCallback)
                    .build();
        }
        return instance;
    }
    //We want to start with a table that already has some Todolists in it instead of an empty table.
    private static RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateTodosAsync(instance).execute();
        }
    };


    private static class PopulateTodosAsync extends AsyncTask<Void, Void, Void> {

        TodoDao todoDao;

        public PopulateTodosAsync(TodoDatabase todoDatabase) {
            this.todoDao = todoDatabase.todoDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todoDao.addTodo(new Todo( "Add your new Tasks Here.", System.currentTimeMillis(),"Default"));
            todoDao.addTodo(new Todo( "Swipe to delete These ones", System.currentTimeMillis(),"Default"));
            todoDao.addTodo(new Todo( "Type code", System.currentTimeMillis(),"Default"));
            todoDao.addTodo(new Todo( "Play Games", System.currentTimeMillis(),"Default"));
            return null;
        }
    }

}
