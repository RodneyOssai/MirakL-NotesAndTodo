package com.figurehowto.mirakl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class CreateEditNoteActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static final String EXTRA_NOTE_ID =
            "com.figurehowto.mirakl.EXTRA_NOTE_ID";
    public static final String EXTRA_NOTE_TITLE =
            "com.figurehowto.mirakl.EXTRA_NOTE_TITLE";
    public static final String EXTRA_NOTE_BODY =
            "com.figurehowto.mirakl.EXTRA_NOTE_BODY";
    public static final String EXTRA_NOTE_STRING_ID =
            "com.figurehowto.mirakl.EXTRA_NOTE_STRING_ID";
    public static final String EXTRA_CREATED_AT =
            "com.figurehowto.mirakl.EXTRA_CREATED_AT";
    public static final String EXTRA_LAST_MODIFIED =
            "com.figurehowto.mirakl.EXTRA_LAST_MODIFIED";
    public static final String EXTRA_ALARM_TIME =
            "com.figurehowto.mirakl.EXTRA_ALARM_TIME";
    ;

    ImageView saveNote, goBack, copyNote, shareNote;
    EditText noteTitleEdittext, noteBodyEdittext;
    TextView createdAtTimestamp, lastModifiedTimestamp;
    float dX;
    float dY;
    int lastAction;
    private long lastModified, createdAt;
    private boolean textIsChanged = false;
    TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //Link XML to Java code
        saveNote = findViewById(R.id.save_note);
        goBack = findViewById(R.id.back_img);
        shareNote = findViewById(R.id.share_note);
        copyNote = findViewById(R.id.copy_note);
        noteTitleEdittext = findViewById(R.id.note_title);
        noteBodyEdittext = findViewById(R.id.note_body);
        final View dragView = findViewById(R.id.draggable_view);
        createdAtTimestamp = findViewById(R.id.created_timestamp);
        lastModifiedTimestamp = findViewById(R.id.last_modified_timestamp);
        lastModified = System.currentTimeMillis();

        createdAtTimestamp.setText("Created at: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date(lastModified)));
        lastModifiedTimestamp.setVisibility(View.INVISIBLE);

        //Instiantate TextWatcher
        textWatcher = new  TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!textIsChanged){
                    textIsChanged = true;
                }
            }
        };

        //Get Height of edittext and use it to position dragview dynamically
        noteTitleEdittext.post(new Runnable() {
            @Override
            public void run() {
                int topMargin = noteBodyEdittext.getHeight(); //height is ready
                int width = noteBodyEdittext.getWidth(); //Width is ready
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = topMargin + 100;
                params.leftMargin = (width / 2) - 175;
                dragView.setLayoutParams(params);

            }
        });

        //Set OnclickListeners
        saveNote.setOnClickListener(this);
        goBack.setOnClickListener(this);
        shareNote.setOnClickListener(this);
        copyNote.setOnClickListener(this);
        dragView.setOnTouchListener(this);

        //Get Edit Note Intent passed from Main activity
        Intent editNote = getIntent();
        if (editNote.hasExtra(EXTRA_NOTE_ID)) {
            lastModified = System.currentTimeMillis();
            noteTitleEdittext.setText(editNote.getStringExtra(EXTRA_NOTE_TITLE));
            noteBodyEdittext.setText(editNote.getStringExtra(EXTRA_NOTE_BODY));
            //Add a TextWatcher to edittext after setting the text so that we can detect user input and save it onbackpressed
            noteBodyEdittext.addTextChangedListener(textWatcher);
            createdAt = editNote.getLongExtra(EXTRA_CREATED_AT, 000000);
            createdAtTimestamp.setText("Created at: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date(createdAt)));
            lastModifiedTimestamp.setVisibility(View.VISIBLE);
            lastModifiedTimestamp.setText("Last Modified: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date(editNote.getLongExtra(EXTRA_LAST_MODIFIED, 000000))));

        }
    }

    @Override
    public void onBackPressed() {
        if (textIsChanged) {
            saveNote();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_note:
                //Do Logic Here
                saveNote();
                break;
            case R.id.back_img:
                //Do logic here
                onBackPressed();
                break;
            case R.id.copy_note:
                copyNoteToClipboard();

                break;
            case R.id.share_note:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, noteBodyEdittext.getText().toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void copyNoteToClipboard() {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("NoteData", noteBodyEdittext.getText().toString());
        assert clipboard != null;
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    private void saveNote() {
        //Extract User data from edittext
        String noteTitle = noteTitleEdittext.getText().toString();
        String noteBody = noteBodyEdittext.getText().toString();
        long c = createdAt;
        if (noteTitle.trim().isEmpty() && noteBody.trim().isEmpty()) {
            Toast.makeText(this, "Nothing to save. Empty note Discarded", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //Put data into an intent and send the data back to main activity
        Intent data = getIntent();
        data.putExtra(EXTRA_NOTE_TITLE, noteTitle);
        data.putExtra(EXTRA_NOTE_BODY, noteBody);
        data.putExtra(EXTRA_LAST_MODIFIED, lastModified);
        data.putExtra(EXTRA_CREATED_AT, c);
        //The main activity needs this id to figure out which ID in the Database it needs to update.
        int id = data.getIntExtra(EXTRA_NOTE_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_NOTE_ID, id);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - motionEvent.getRawX();
                dY = view.getY() - motionEvent.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(motionEvent.getRawY() + dY);
                view.setX(motionEvent.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(CreateEditNoteActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                break;

            default:
                return false;
        }
        return true;
    }
}