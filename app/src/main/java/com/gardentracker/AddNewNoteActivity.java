package com.gardentracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;


public class AddNewNoteActivity extends AppCompatActivity {

    Shared shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        shared = new Shared();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action) {
            final String description = ((EditText) findViewById(R.id.editDescription)).getText().toString();
            if (description.length() == 0)
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_form),Toast.LENGTH_LONG).show();
            else {
                final Dialog dialog = shared.openSimpleDialog(AddNewNoteActivity.this,getResources().getString(R.string.add_note_dialog_title),
                        getResources().getString(R.string.add_note_dialog_question),
                        getResources().getString(R.string.yes),getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNewNote(description);
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
            }
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(AddNewNoteActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNewNote(String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Note.DESCRIPTION,description);
        contentValues.put(Contract.Note.CHANGED,System.currentTimeMillis()/1000);

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                Toast.makeText(AddNewNoteActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
                finish();
            }
        };
        asyncQueryHandler.startInsert(0,null,Contract.Note.CONTENT_URI,contentValues);
    }
}
