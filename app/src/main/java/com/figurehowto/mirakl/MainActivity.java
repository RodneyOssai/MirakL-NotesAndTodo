package com.figurehowto.mirakl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CREATE_NOTE_REQUEST = 60;
    public static final int EDIT_NOTE_REQUEST = 61;
    public static final int EDIT_TODO_REQUEST = 62;
    NoteViewModel noteViewModel;
    TodoViewModel todoViewModel;
    FloatingActionButton add, addNotes, addList;
    TextView addNoteLabel, addListLabel;
    LinearLayout fabLayout1, fabLayout2;
    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    private boolean isFABOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Draw and Update recycler view
        recyclerView = findViewById(R.id.savednotes);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter();
        recyclerView.setAdapter(notesAdapter);
        //assign Viewmodel and observe livedata.
        noteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        todoViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(TodoViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //Update Recycler view here
                notesAdapter.submitList(notes);

            }
        });
        //Item touch helper is used to implement the swipe to delete functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = notesAdapter.getNoteAt(viewHolder.getAdapterPosition());
                String noteID = note.getNoteID();
                if(noteID != null){
                    todoViewModel.getTodosByNoteId(noteID);
                }
                noteViewModel.delete(note);

                //add the snackbar and the undo button
                Snackbar.make(findViewById(R.id.main_activity), "Note Deleted.", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                noteViewModel.insert(note);
                            }
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);

        //Link XML to Java code
        //Floating Action Button
        add = findViewById(R.id.add);
        addNotes = findViewById(R.id.add_note);
        addList = findViewById(R.id.add_list);

        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout2 = findViewById(R.id.fabLayout2);
        addNoteLabel = findViewById(R.id.add_note_label);
        addListLabel = findViewById(R.id.todo_list_label);

        //Set OnClickListeners
        add.setOnClickListener(this);
        addNotes.setOnClickListener(this);
        addList.setOnClickListener(this);
        addNoteLabel.setOnClickListener(this);
        addListLabel.setOnClickListener(this);

        notesAdapter.setOnItemClickListener(new NotesAdapter.onItemClickListener() {
            @Override
            public void OnItemClick(Note note) {
                //Type 2 note is a Todolist while type 1 note is the normal note
                if (note.getType() == 2) {
                    Intent editTodo = new Intent(MainActivity.this, CreateListActivity.class);
                    editTodo.putExtra(CreateEditNoteActivity.EXTRA_NOTE_ID, note.getId());
                    editTodo.putExtra(CreateEditNoteActivity.EXTRA_NOTE_STRING_ID, note.getNoteID());
                    editTodo.putExtra(CreateEditNoteActivity.EXTRA_NOTE_TITLE, note.getNote_title());
                    editTodo.putExtra(CreateEditNoteActivity.EXTRA_ALARM_TIME, note.getAlarmTime());
                    startActivityForResult(editTodo, EDIT_TODO_REQUEST);
                } else {
                    Intent editNote = new Intent(MainActivity.this, CreateEditNoteActivity.class);
                    editNote.putExtra(CreateEditNoteActivity.EXTRA_NOTE_ID, note.getId());
                    editNote.putExtra(CreateEditNoteActivity.EXTRA_NOTE_TITLE, note.getNote_title());
                    editNote.putExtra(CreateEditNoteActivity.EXTRA_NOTE_BODY, note.getNote_body());
                    editNote.putExtra(CreateEditNoteActivity.EXTRA_CREATED_AT, note.getCreatedAt());
                    editNote.putExtra(CreateEditNoteActivity.EXTRA_LAST_MODIFIED, note.getLastModified());
                    startActivityForResult(editNote, EDIT_NOTE_REQUEST);
                }
            }
        });

    }


    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        addListLabel.setVisibility(View.VISIBLE);
        addNoteLabel.setVisibility(View.VISIBLE);
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        addListLabel.setVisibility(View.GONE);
        addNoteLabel.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Recieve data from add note activity intent
        if (requestCode == CREATE_NOTE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_TITLE);
            String body = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_BODY);
            long lastModified = data.getLongExtra(CreateEditNoteActivity.EXTRA_LAST_MODIFIED,System.currentTimeMillis());
            //Use User Input to create a new Note Object
            Note newNote = new Note(title, body, 1,lastModified);
            newNote.setCreatedAt(lastModified);
            //Insert the note object into the DB using our ViewModel.
            noteViewModel.insert(newNote);
            Toast.makeText(this, "Note Saved Successfully", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(CreateEditNoteActivity.EXTRA_NOTE_ID, -1);
            if (id == -1) {
                //Somthing went wring
                return;
            }
            String title = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_TITLE);
            String body = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_BODY);
            long lastModified = data.getLongExtra(CreateEditNoteActivity.EXTRA_LAST_MODIFIED,System.currentTimeMillis());
            long createdAt = data.getLongExtra(CreateEditNoteActivity.EXTRA_CREATED_AT,000000);
            //Use User Input to edit Note Object
            Note editedNote = new Note(title, body, 1,lastModified);
            editedNote.setId(id);
            editedNote.setCreatedAt(createdAt);
            //Edit the note object in the DB using our ViewModel.
            noteViewModel.update(editedNote);
            Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == EDIT_TODO_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(CreateEditNoteActivity.EXTRA_NOTE_ID, -1);
            if (id == -1) {
                //Somthing went wrong
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_TITLE);
            String body = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_BODY);
            String noteStringId = data.getStringExtra(CreateEditNoteActivity.EXTRA_NOTE_STRING_ID);
            long lastModified = data.getLongExtra(CreateEditNoteActivity.EXTRA_LAST_MODIFIED,System.currentTimeMillis());
            long alarmTime = data.getLongExtra(CreateEditNoteActivity.EXTRA_ALARM_TIME,0L);

            //Use User Input to edit Note Object
            Note editedTodo = new Note(title, body, 2,lastModified);
            editedTodo.setId(id);
            editedTodo.setNoteID(noteStringId);
            editedTodo.setAlarmTime(alarmTime);
            //Edit the note object in the DB using our ViewModel.
            noteViewModel.update(editedTodo);
            Toast.makeText(this, "Todo Updated Successfully", Toast.LENGTH_SHORT).show();
        }
        //Resultcode for backpressed == 0
        //Rembe}else{r to implement save note on back pressed.

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;
            case R.id.add_note:
                //Do logic here
                Intent openCreateNoteActivity = new Intent(MainActivity.this, CreateEditNoteActivity.class);
                startActivityForResult(openCreateNoteActivity, CREATE_NOTE_REQUEST);
                closeFABMenu();
                break;
            case R.id.add_list:
                //Do logic here
                Intent openCreateListActivity = new Intent(MainActivity.this, CreateListActivity.class);
                startActivity(openCreateListActivity);
                closeFABMenu();
                break;
            case R.id.add_note_label:
                openCreateNoteActivity = new Intent(MainActivity.this, CreateEditNoteActivity.class);
                startActivityForResult(openCreateNoteActivity, CREATE_NOTE_REQUEST);
                closeFABMenu();
                break;
            case R.id.todo_list_label:
                openCreateListActivity = new Intent(MainActivity.this, CreateListActivity.class);
                startActivity(openCreateListActivity);
                closeFABMenu();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}