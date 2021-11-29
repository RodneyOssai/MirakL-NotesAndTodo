package com.figurehowto.mirakl;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Note.class,version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    //Singleton, we cannot create multiple instances on this db
    private static NoteDatabase instance;

    public abstract NoteDao notedao();

    //synchronized means that only one thread at a time can access this method
    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,
                    "notes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    //We want to start with a table that already has some notes in it instead of an empty table.
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsync(instance).execute();
        }
    };



    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{
            private NoteDao noteDao;

        public PopulateDbAsync(NoteDatabase noteDatabase) {
            this.noteDao = noteDatabase.notedao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Note A","In my case, after adding about half a dozen attributes (described below) to force the textview to wrap text over multiple lines, without success, I noticed I had an attribute Apparently it had been added by the Android Studio desig",1,System.currentTimeMillis()));
            noteDao.insert(new Note("Note B","The maxLines count seemed to be the random final piece that made my TextView wrap.",1,System.currentTimeMillis()));
            noteDao.insert(new Note("Note C","TextView you want to wrap is in. (0-indexed)) I had initially gotten it to sort of work with",1,System.currentTimeMillis()));
            noteDao.insert(new Note("Note D","This just causes the TextView to expand to take as much space as possible, which is the same as setting the Height to match_parent. Not quite the same thing as OP wanted",1,System.currentTimeMillis()));
            noteDao.insert(new Note("Note E","OK guys the truth is somewhere in the middle cause you have to see the issue from the parent's view and child's. The solution below works ONLY when spinner mode ",1,System.currentTimeMillis()));
            noteDao.insert(new Note("Note F","Tried so man 'dialog' mode",1,System.currentTimeMillis()));
            return null;
        }
    }
}
