package com.figurehowto.mirakl;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
//Repository provides another abstraction layer btw the data sources and the viewmodel example DB and internet
// The repository uses NoteDao(Data access Object to retrieve DB the entries from our notes table)
public class NoteRepository {

    private NoteDao notedao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        //We get create an instance of the database in the repository
        NoteDatabase database = NoteDatabase.getInstance(application);
        //From the database instance we call the notedao() method in NoteDatabase
        notedao = database.notedao();
        //Using the NoteDao getAllNotes method we retieive all notes from the DB
        allNotes = notedao.getAllNotes();
    }
    /* Here we create methods for all our different database operations*/
    public void insert(Note note){
        new insertNoteAsync(notedao).execute(note);
    }
    public void delete(Note note){
        new deleteNoteAsync(notedao).execute(note);
    }
    public void update(Note note){
        new updateNoteAsync(notedao).execute(note);
    }
    public void deleteAllNotes(){
        new deleteAllNotesAsync(notedao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public static class insertNoteAsync extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        public insertNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }
    public static class deleteNoteAsync extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        public deleteNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
    public static class updateNoteAsync extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        public updateNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }
    public static class deleteAllNotesAsync extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        public deleteAllNotesAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}
