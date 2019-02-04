package com.gardentracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gardentracker.classes.Note;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;


public class DetailNoteActivity extends AppCompatActivity {

    Note note = new Note();
    Shared shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_edit) {
            note.setDescription(((EditText) findViewById(R.id.editDescription)).getText().toString());
            if (note.getDescription().length() == 0)
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_form),Toast.LENGTH_LONG).show();
            else {
                final Dialog dialog = shared.openSimpleDialog(DetailNoteActivity.this,getResources().getString(R.string.save_note_dialog_title),
                        getResources().getString(R.string.save_note_dialog_question),
                        getResources().getString(R.string.yes),getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNote();
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
            }
        } else if (id == R.id.action_remove) {
            final Dialog dialog = shared.openSimpleDialog(DetailNoteActivity.this,getResources().getString(R.string.remove_note_dialog_title),
                    getResources().getString(R.string.remove_note_dialog_question),
                    getResources().getString(R.string.yes),getResources().getString(R.string.no));
            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeNote();
                    dialog.dismiss();
                }
            });
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
            dialog.show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailNoteActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUI() {
        shared = new Shared();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",-1);
        if (id != -1) {
            note.setId(id);

            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                @Override
                protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                    super.onQueryComplete(token, cookie, cursor);

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        note.setDescription(cursor.getString(cursor.getColumnIndex(Contract.Note.DESCRIPTION)));
                        note.setChanged(cursor.getLong(cursor.getColumnIndex(Contract.Note.CHANGED)));

                        ((TextView)findViewById(R.id.editDescription)).setText(note.getDescription());
                    }
                }
            };
            asyncQueryHandler.startQuery(0,null,Uri.withAppendedPath(Contract.Note.CONTENT_URI,String.valueOf(id)),null,null,null,null);
        }
    }

    private void saveNote() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Note.DESCRIPTION,note.getDescription());
        contentValues.put(Contract.Note.CHANGED,System.currentTimeMillis()/1000);

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onUpdateComplete(int token, Object cookie, int result) {
                super.onUpdateComplete(token, cookie, result);
                Toast.makeText(DetailNoteActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
                finish();
            }
        };
        asyncQueryHandler.startInsert(0,null,Uri.withAppendedPath(Contract.Note.CONTENT_URI,String.valueOf(note.getId())),contentValues);
    }

    private void removeNote() {
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Toast.makeText(DetailNoteActivity.this,getResources().getString(R.string.deleted),Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        asyncQueryHandler.startDelete(0,null,Uri.withAppendedPath(Contract.Note.CONTENT_URI,String.valueOf(note.getId())),null,null);
    }
}
