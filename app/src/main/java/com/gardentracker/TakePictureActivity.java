package com.gardentracker;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.CameraEngine;
import com.commonsware.cwac.cam2.OrientationLockMode;
import com.gardentracker.classes.Photo;
import com.gardentracker.adapters.PhotoGalleryAdapter;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TakePictureActivity extends AppCompatActivity {

    ArrayList<Photo> photos = null;
    private static final int CAMERA_REQUEST = 1888;
    PhotoGalleryAdapter adapter = null;
    RecyclerView recyclerView = null;
    final int COUNT_OF_IMAGES_IN_ROW = 2;
    FloatingActionButton fab;
    Shared shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        shared = new Shared();
        takePicture();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(TakePictureActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePicture() {
        String photoName = String.format(getResources().getString(R.string.image_name),System.currentTimeMillis());

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(dir,photoName);

        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        Intent intent = new CameraActivity.IntentBuilder(this)
                .skipConfirm()
                .forceEngine(CameraEngine.ID.CLASSIC)
                .to(image)
                .updateMediaStore()
                .quality(AbstractCameraActivity.Quality.HIGH)
                .orientationLockMode(OrientationLockMode.LANDSCAPE)
                .build();
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && data != null)
            savePhoto(data);
        else if (requestCode == CAMERA_REQUEST && resultCode == 0 && data == null)
            finish();
    }

    private void savePhoto(Intent data){
        final Uri uri = data.getData();
        byte[] miniature = getMiniature(uri);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Photo.NAME, uri.getLastPathSegment());
        contentValues.put(Contract.Photo.PHOTO_URI, uri.toString());
        contentValues.put(Contract.Photo.MINIATURE,miniature);
        contentValues.put(Contract.Photo.DELETED,0);
        contentValues.put(Contract.Photo.CHANGED, System.currentTimeMillis());

        @SuppressLint("HandlerLeak") AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()){
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri u) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
                finish();
            }
        };
        queryHandler.startInsert(0, null, Contract.Photo.CONTENT_URI, contentValues);
    }

    private byte[] getMiniature(Uri uri) {
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            int imageWidth = imageBitmap.getWidth();
            int imageHeight = imageBitmap.getHeight();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            double displayWidth = size.x;
            double miniatureWidth = displayWidth/COUNT_OF_IMAGES_IN_ROW;
            double ratio = imageWidth / miniatureWidth;
            double miniatureHeight = imageHeight / ratio;

            int max = Math.max((int)miniatureWidth,(int)miniatureHeight);

            Bitmap bitmap = shared.getScaledDownBitmap(imageBitmap,max,true);
            return getBytes(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }
}
