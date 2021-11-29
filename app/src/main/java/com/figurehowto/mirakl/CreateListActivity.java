package com.figurehowto.mirakl;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.figurehowto.mirakl.CreateEditNoteActivity.EXTRA_ALARM_TIME;
import static com.figurehowto.mirakl.CreateEditNoteActivity.EXTRA_NOTE_BODY;
import static com.figurehowto.mirakl.CreateEditNoteActivity.EXTRA_NOTE_ID;
import static com.figurehowto.mirakl.CreateEditNoteActivity.EXTRA_NOTE_STRING_ID;
import static com.figurehowto.mirakl.CreateEditNoteActivity.EXTRA_NOTE_TITLE;

public class CreateListActivity extends AppCompatActivity implements View.OnClickListener, BtmShtDialog.BtmShtListener {
    //Declare Field Variables
    TodoViewModel todoViewModel;
    NoteViewModel noteViewModel;
    ImageView saveNote, goBack, addItem;
    Button addAlarm;
    EditText listTitleEdittext, actualTaskEdittext;
    RecyclerView recyclerView;
    TodoAdapter todoAdapter;
    int editId,alarmID;
    private long createdAt, alarmTime;
    String noteID, noteTitle;
    int day, month, hour, minute, year;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        //Create a new SharedPreference file
        sharedPref = getBaseContext().getSharedPreferences(
                "com.figurehowto.mirakl.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

        //Create An ID for the TODOList as it would be saved as a note
        RandomString randomString = new RandomString(12);
        noteID = randomString.nextString();
        //Link XML to Java code
        saveNote = findViewById(R.id.save_note);
        goBack = findViewById(R.id.back_img);
        addItem = findViewById(R.id.add_item);
        listTitleEdittext = findViewById(R.id.task_title);
        actualTaskEdittext = findViewById(R.id.actual_todo_task);
        addAlarm = findViewById(R.id.add_alarm);
        createdAt = System.currentTimeMillis();
        //Initialize the datePickerDialog
        datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                day = datePicker.getDayOfMonth();
                month = datePicker.getMonth();
                year = datePicker.getYear();

                timePickerDialog.show();
            }
        });
        // Initialize the timePickerDialog
        Calendar c = Calendar.getInstance();//Get an instance on calender and get current time of the device
        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour = timePicker.getHour();
                minute = timePicker.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute, 0);
                long userTime = calendar.getTimeInMillis();
                if (userTime < System.currentTimeMillis()) {
                    Toast.makeText(CreateListActivity.this, "You can't set a todo for the past", Toast.LENGTH_SHORT).show();
                } else {
                    alarmTime = userTime;
                    //First time an alarm is created , alarmID is definitely 0 ie Field Variable default for int;
                    if(alarmID == 0){
                        //Generate Random Number and assign as alarm ID = Min + (int)(Math.random() * ((Max - Min) + 1))
                        alarmID = 1000 + (int)(Math.random() * ((2000 - 1000) + 1));
                    }else{
                        //Retrieve the alarmID saved in sharedPref using noteID as key
                        alarmID = sharedPref.getInt(noteID,0);
                    }
                    //Run the method responsible for starting the alarm.
                    startAlarm(calendar,alarmID);
                }

            }
        }, hr, min, DateFormat.is24HourFormat(getBaseContext()));


        //Set OnClicklisteners
        addItem.setOnClickListener(this);
        saveNote.setOnClickListener(this);
        goBack.setOnClickListener(this);
        addAlarm.setOnClickListener(this);
        addAlarm.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Send Alarm ID to th BottomSheet Dialog fragment so it can be used later
                Bundle bundle = new Bundle();
                alarmID = sharedPref.getInt(noteID,0);
                bundle.putInt("alarmId", alarmID);
                BtmShtDialog bottomsheet = new BtmShtDialog();
                bottomsheet.setArguments(bundle);
                bottomsheet.show(getSupportFragmentManager(),"EditAlarmBottomsheet");
                return false;
            }
        });

        //Draw and Update recycler view
        recyclerView = findViewById(R.id.tasks_recycler);
        todoAdapter = new TodoAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(todoAdapter);
        //assign Viewmodel and observe livedata.
        noteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        todoViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(TodoViewModel.class);


        //Get Edit Note Intent passed from Main activity
        Intent editTodo = getIntent();
        if (editTodo.hasExtra(EXTRA_NOTE_ID)) {
            noteTitle = editTodo.getStringExtra(EXTRA_NOTE_TITLE);
            listTitleEdittext.setText(noteTitle);
            alarmTime = editTodo.getLongExtra(EXTRA_ALARM_TIME, 0L);
            if (alarmTime != 0L) {
                addAlarm.setText(new SimpleDateFormat("dd MMM, hh:mm a").format(new Date(alarmTime)));
                if (alarmTime < System.currentTimeMillis()) {
                    addAlarm.setText("Alarm Fired!!");
                }
            }

            //This part runs when it is an EditTodo Request
            editId = editTodo.getIntExtra(EXTRA_NOTE_ID, -1);
            //For an EditTodo request reassign the noteID to the one given to us by the intent
            noteID = editTodo.getStringExtra(EXTRA_NOTE_STRING_ID);
            todoViewModel.getTodosByNoteId(noteID).observe(this, new Observer<List<Todo>>() {
                @Override
                public void onChanged(List<Todo> todoItems) {
                    //Update Recycler view here
                    todoAdapter.submitList(todoItems);
                }
            });
        } else {
            //This part runs when it is not an EditTodo Request i.e EditTodo wasn't passed from MainActivity.
            editId = 81345;
            todoViewModel.getTodosByNoteId(noteID).observe(this, new Observer<List<Todo>>() {
                @Override
                public void onChanged(List<Todo> todoItems) {
                    //Update Recycler view here
                    todoAdapter.submitList(todoItems);
                }
            });
        }

        //Item touch helper is used to implement the swipe to delete functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Todo todo = (todoAdapter.getTodoAt(viewHolder.getAdapterPosition()));
                todoViewModel.deleteTodo(todo);
                //add the snackbar and the undo button
                Snackbar.make(findViewById(R.id.create_and_edit_todo), "Task Deleted.", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                todoViewModel.addTodo(todo);
                            }
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);

        //Listen for when the user taps the checkbox and take necessary actions.
        todoAdapter.setOnTodoCompletedListener(new TodoAdapter.onTodoCompletedListener() {
            @Override
            public void todoCompleted(Todo todo, boolean isChecked) {
                String s = todo.getTask_todo();
                String y = todo.getNoteId();
                long z = System.currentTimeMillis();
                int b = todo.getId();
                if (isChecked) {
                    todoViewModel.deleteTodo(todo);
                    Todo completedTodo = new Todo(s, z, y);
                    completedTodo.setCompleted(isChecked);
                    todoViewModel.addTodo(completedTodo);

                } else {
                    todoViewModel.deleteTodo(todo);
                    Todo completedTodo = new Todo(s, z, y);
                    completedTodo.setCompleted(false);
                    todoViewModel.addTodo(completedTodo);

                }
            }
        });

    }

    private void startAlarm(Calendar calendar,int alarmID) {
        //Save the alarm ID to phone memory using Shared pref.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(noteID, alarmID);
        editor.apply();
        //get the Alarm Notification Text
        String notifText = "Remember your Task";
        if (listTitleEdittext.getText().toString()!=""){
            notifText = listTitleEdittext.getText().toString();
        }
        //Set an alarm to start.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        intent.putExtra("notifText",notifText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //Change Text in the button to alarm we just set
        addAlarm.setText(new SimpleDateFormat("dd MMM, hh:mm a").format(new Date(calendar.getTimeInMillis())));
        Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(int alarmID) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, intent, 0);
        alarmManager.cancel(pendingIntent);
        addAlarm.setText("When?");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //addTodoitem();
        saveNote.performClick();
    }

    //Function to add a TodoItem into the DB
    private void addTodoitem() {
        //Extract User data from edittext
        String listTitle = listTitleEdittext.getText().toString();
        String todoTask = actualTaskEdittext.getText().toString();
        if (listTitle.trim().isEmpty() && todoTask.trim().isEmpty()) {
            Toast.makeText(this, "No Task to add.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //Put extracted data into a single TodoList Item and add the item to the database using ViewModel;
        //Use User Input to create a new Note Object
        Todo newTodoItem = new Todo(todoTask, createdAt, noteID);
        //Insert the note object into the DB using our ViewModel.
        todoViewModel.addTodo(newTodoItem);
        actualTaskEdittext.getText().clear();
    }

    //Function to turn the contents of the Todolist to a Nice preview
    public String translateToNotebody() {
        int listlength = todoAdapter.getItemCount();
        Toast.makeText(getBaseContext(), String.valueOf(listlength), Toast.LENGTH_SHORT).show();
        int i = 0;
        String list_item;
        StringBuilder listBody = new StringBuilder("");

        do {
            list_item = Html.fromHtml("&#x25A2; &nbsp;", Html.FROM_HTML_MODE_COMPACT).toString() + ((TextView) Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(i)).itemView.findViewById(R.id.list_item)).getText().toString() + "\n";
            listBody.append(list_item);
            i++;
        } while (i <= listlength - 1);


        return listBody.toString();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_note:
                //Do Logic Here
                //81345 id means it is not an edit note request
                if ((todoAdapter.getItemCount()) == 0) {
                    Toast.makeText(this, "No list to save", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (editId == 81345) {
                    //Somthing went wrong
                    Note note = new Note(listTitleEdittext.getText().toString(), translateToNotebody(), 2, createdAt);
                    note.setNoteID(noteID);
                    if (alarmTime != 0L) {
                        note.setAlarmTime(alarmTime);
                    }
                    noteViewModel.insert(note);
                    addTodoitem();
                    Toast.makeText(this, "Todo Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    //Put data into an intent and send the data back to main activity
                    //This part runs when it is an EditTodoRequest
                    Intent data = getIntent();
                    data.putExtra(EXTRA_NOTE_TITLE, listTitleEdittext.getText().toString());
                    data.putExtra(EXTRA_NOTE_BODY, translateToNotebody());
                    data.putExtra(EXTRA_NOTE_STRING_ID, noteID);
                    data.putExtra(EXTRA_ALARM_TIME, alarmTime);

                    //The main activity needs this id to figure out which ID in the Database it needs to update.
                    if (editId != -1) {
                        data.putExtra(EXTRA_NOTE_ID, editId);
                    }
                    setResult(RESULT_OK, data);
                }
                finish();
                break;
            case R.id.back_img:
                //Do logic here
                super.onBackPressed();
                break;
            case R.id.add_item:
                //Do logic here
                addTodoitem();
                break;
            case R.id.add_alarm:
                //Show DatePicker dialog
                datePickerDialog.show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onChangeClicked() {
        addAlarm.performClick();
    }

    @Override
    public void onCancelClicked(int alarmID) {
        cancelAlarm(alarmID);
    }
}
