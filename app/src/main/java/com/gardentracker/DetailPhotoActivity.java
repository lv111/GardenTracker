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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gardentracker.adapters.CustomSimpleAdapter;
import com.gardentracker.adapters.PhotoDescriptionAdapter;
import com.gardentracker.classes.Photo;
import com.gardentracker.classes.PhotoDescription;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailPhotoActivity extends AppCompatActivity {

    Photo photo;
    Shared shared;
    PhotoDescriptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUI();
    }

    private void setUI() {
        photo = new Photo();
        shared = new Shared();
        Intent intent = getIntent();
        Uri uri;
        try {
            uri = Uri.parse(intent.getStringExtra("uri"));
        } catch (NullPointerException e) {
            int id = intent.getIntExtra("id", -1);
            uri = Uri.withAppendedPath(Contract.Photo.CONTENT_URI, String.valueOf(id));
        }

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();

                photo.setId(cursor.getInt(cursor.getColumnIndex(Contract.Photo._ID)));
                photo.setName(cursor.getString(cursor.getColumnIndex(Contract.Photo.NAME)));
                photo.setPhotoUri(cursor.getString(cursor.getColumnIndex(Contract.Photo.PHOTO_URI)));
                photo.setChanged(cursor.getLong(cursor.getColumnIndex(Contract.Photo.CHANGED)));

                setTitle(photo.getName());
                Picasso.with(DetailPhotoActivity.this).load(photo.getPhotoUri()).into((ImageView) findViewById(R.id.imageView));

                String selection = Contract.PhotoDescription.ID_PHOTO + "=?";
                String[] selectionArgs = new String[]{String.valueOf(photo.getId())};
                @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                        super.onQueryComplete(token, cookie, cursor);

                        ArrayList<PhotoDescription> descriptions = new ArrayList<>();
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            do {
                                descriptions.add(
                                        new PhotoDescription(
                                                cursor.getInt(cursor.getColumnIndex(Contract.PhotoDescription._ID)),
                                                cursor.getString(cursor.getColumnIndex(Contract.PhotoDescription.DESCRIPTION)),
                                                cursor.getLong(cursor.getColumnIndex(Contract.PhotoDescription.CHANGED))));
                            } while (cursor.moveToNext());
                        }
                        photo.setDescriptions(descriptions);

                        setAdapterForListView();
                    }
                };
                asyncQueryHandler.startQuery(0, null, Contract.PhotoDescription.CONTENT_URI, null, selection, selectionArgs, null);
            }
        };
        asyncQueryHandler.startQuery(0, null, uri, null,null,null, null);

        (findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = shared.openSimpleDialogEditText(DetailPhotoActivity.this, getResources().getString(R.string.here_you_can_add_note), null);
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addNote(dialog);
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
            }
        });
    }

    private void addNote(Dialog dialog) {
        final String note = ((EditText) dialog.findViewById(R.id.editText)).getText().toString();

        if (note.length() == 0)
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.note_not_filled),Toast.LENGTH_SHORT).show();
        else {
            final long time = System.currentTimeMillis()/1000;
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.PhotoDescription.ID_PHOTO,photo.getId());
            contentValues.put(Contract.PhotoDescription.DESCRIPTION,note);
            contentValues.put(Contract.PhotoDescription.CHANGED,time);

            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    super.onInsertComplete(token, cookie, uri);
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.saved),Toast.LENGTH_SHORT).show();

                    photo.addDescriptions(
                            new PhotoDescription(Integer.parseInt(uri.getLastPathSegment()),note,time));
                    adapter.updateData(photo.getDescriptions());
                }
            };
            asyncQueryHandler.startInsert(0,null,Contract.PhotoDescription.CONTENT_URI,contentValues);
            dialog.dismiss();
        }
    }

    private void updateNote(Dialog dialog, final int position, String strDescription) {
        final PhotoDescription description = photo.getDescriptions().get(position);
        final String note = ((EditText) dialog.findViewById(R.id.editText)).getText().toString();

        if (!note.equals(strDescription)) {
            if (note.length() == 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.note_not_filled), Toast.LENGTH_SHORT).show();
            else {
                final long time = System.currentTimeMillis() / 1000;
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.PhotoDescription.DESCRIPTION, note);
                contentValues.put(Contract.PhotoDescription.CHANGED, time);

                @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onUpdateComplete(int token, Object cookie, int result) {
                        super.onUpdateComplete(token, cookie, result);

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                        description.setDescription(note);
                        adapter.updateData(photo.getDescriptions());
                    }
                };
                asyncQueryHandler.startUpdate(0, null, Uri.withAppendedPath(Contract.PhotoDescription.CONTENT_URI, String.valueOf(description.getId())), contentValues, null, null);
            }
        }
        dialog.dismiss();
    }

    private void removeNote(final int position) {
        PhotoDescription photoDescriptionToRemove = photo.getDescriptions().get(position);
        Uri uri = Uri.withAppendedPath(Contract.PhotoDescription.CONTENT_URI,String.valueOf(photoDescriptionToRemove.getId()));

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                photo.getDescriptions().remove(position);
                adapter.updateData(photo.getDescriptions());
            }
        };
        asyncQueryHandler.startDelete(0,null,uri,null,null);
    }

    private void setAdapterForListView() {
        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new PhotoDescriptionAdapter(DetailPhotoActivity.this,photo.getDescriptions());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String description = photo.getDescriptions().get(position).getDescription();
                final Dialog dialog = shared.openSimpleDialogEditText(DetailPhotoActivity.this, null, description);
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNote(dialog, position, description);
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = shared.openSimpleDialog(DetailPhotoActivity.this,getResources().getString(R.string.remove_photo_note_dialog_title),
                        getResources().getString(R.string.remove_photo_note_dialog_question),null,null);
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeNote(position);
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail_only_remove, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_remove) {
            final Dialog dialog = shared.openSimpleDialog(DetailPhotoActivity.this,getResources().getString(R.string.remove_photo_dialog_title),
                    getResources().getString(R.string.remove_photo_dialog_question),
                    getResources().getString(R.string.yes),getResources().getString(R.string.no));
            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePhoto();
                    dialog.dismiss();
                }
            });
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
            dialog.show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailPhotoActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void removePhoto() {
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Toast.makeText(DetailPhotoActivity.this,getResources().getString(R.string.deleted),Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        asyncQueryHandler.startDelete(0,null,Uri.withAppendedPath(Contract.Photo.CONTENT_URI,String.valueOf(photo.getId())),null,null);
    }
}
