package com.gardentracker.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.gardentracker.classes.Photo;

public class GardenTrackerContentProvider extends ContentProvider {
    private DatabaseOpenHelper databaseOpenHelper;

    static final String AUTHORITY = "com.gardentracker";
    static final int MAINTENANCE = 1;
    static final int MAINTENANCE_ID = 2;
    static final int PHOTO = 3;
    static final int PHOTO_ID = 4;
    static final int PHOTO_DESCRIPTION = 5;
    static final int PHOTO_DESCRIPTION_ID = 6;
    static final int NOTE = 7;
    static final int NOTE_ID = 8;
    static final int WEATHER = 9;
    static final int WEATHER_ID = 10;
    static final int SETTINGS = 11;
    static final int SETTINGS_ID = 12;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(AUTHORITY, "maintenance", MAINTENANCE);
        mUriMatcher.addURI(AUTHORITY, "maintenance/#", MAINTENANCE_ID);
        mUriMatcher.addURI(AUTHORITY, "photo", PHOTO);
        mUriMatcher.addURI(AUTHORITY, "photo/#", PHOTO_ID);
        mUriMatcher.addURI(AUTHORITY, "photo_description", PHOTO_DESCRIPTION);
        mUriMatcher.addURI(AUTHORITY, "photo_description/#", PHOTO_DESCRIPTION_ID);
        mUriMatcher.addURI(AUTHORITY, "note", NOTE);
        mUriMatcher.addURI(AUTHORITY, "note/#", NOTE_ID);
        mUriMatcher.addURI(AUTHORITY, "weather", WEATHER);
        mUriMatcher.addURI(AUTHORITY, "weather/#", WEATHER_ID);
        mUriMatcher.addURI(AUTHORITY, "settings", SETTINGS);
        mUriMatcher.addURI(AUTHORITY, "settings/#", SETTINGS_ID);
    }


    @Override
    public boolean onCreate() {
        databaseOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MAINTENANCE: {
                long id = db.insert(Contract.Maintenance.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(Contract.Maintenance.CONTENT_URI, null);
                return Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI, String.valueOf(id));
            }
            case PHOTO: {
                long id = db.insert(Contract.Photo.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(Contract.Photo.CONTENT_URI, null);
                return Uri.withAppendedPath(Contract.Photo.CONTENT_URI, String.valueOf(id));
            }
            case PHOTO_DESCRIPTION: {
                long id = db.insert(Contract.PhotoDescription.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(Contract.PhotoDescription.CONTENT_URI, null);
                return Uri.withAppendedPath(Contract.PhotoDescription.CONTENT_URI, String.valueOf(id));
            }
            case NOTE: {
                long id = db.insert(Contract.Note.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(Contract.Note.CONTENT_URI, null);
                return Uri.withAppendedPath(Contract.Note.CONTENT_URI, String.valueOf(id));
            }
            default:
                return null;
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (mUriMatcher.match(uri)) {
            case MAINTENANCE: {
                queryBuilder.setTables(Contract.Maintenance.TABLE_NAME);
                break;
            }
            case MAINTENANCE_ID: {
                queryBuilder.setTables(Contract.Maintenance.TABLE_NAME);
                String id = uri.getLastPathSegment();
                selection = Contract.Maintenance._ID + "=?";
                selectionArgs = new String[]{id};
                break;
            }
            case PHOTO: {
                queryBuilder.setTables(Contract.Photo.TABLE_NAME);
                break;
            }
            case PHOTO_ID: {
                queryBuilder.setTables(Contract.Photo.TABLE_NAME);
                String id = uri.getLastPathSegment();
                selection = Contract.Photo._ID + "=?";
                selectionArgs = new String[]{id};
                break;
            }
            case PHOTO_DESCRIPTION: {
                queryBuilder.setTables(Contract.PhotoDescription.TABLE_NAME);
                break;
            }
            case PHOTO_DESCRIPTION_ID: {
                queryBuilder.setTables(Contract.PhotoDescription.TABLE_NAME);
                String id = uri.getLastPathSegment();
                selection = Contract.PhotoDescription._ID + "=?";
                selectionArgs = new String[]{id};
                break;
            }
            case NOTE: {
                queryBuilder.setTables(Contract.Note.TABLE_NAME);
                break;
            }
            case NOTE_ID: {
                queryBuilder.setTables(Contract.Note.TABLE_NAME);
                String id = uri.getLastPathSegment();
                selection = Contract.Note._ID + "=?";
                selectionArgs = new String[]{id};
                break;
            }
            case SETTINGS: {
                queryBuilder.setTables(Contract.Settings.TABLE_NAME);
                break;
            }
        }

        if (queryBuilder.getTables() != null)
            return queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        else return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MAINTENANCE_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.delete(Contract.Maintenance.TABLE_NAME, Contract.Maintenance._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Maintenance.CONTENT_URI, null);
                return affectedRows;
            }
            case PHOTO_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.delete(Contract.Photo.TABLE_NAME, Contract.Photo._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Photo.CONTENT_URI, null);
                return affectedRows;
            }
            case PHOTO_DESCRIPTION_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.delete(Contract.PhotoDescription.TABLE_NAME, Contract.PhotoDescription._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.PhotoDescription.CONTENT_URI, null);
                return affectedRows;
            }
            case NOTE_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.delete(Contract.Note.TABLE_NAME, Contract.Note._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Note.CONTENT_URI, null);
                return affectedRows;
            }
            default:
                return -1;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MAINTENANCE_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.update(Contract.Maintenance.TABLE_NAME,values,Contract.Maintenance._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Maintenance.CONTENT_URI, null);
                return affectedRows;
            }
            case PHOTO_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.update(Contract.Photo.TABLE_NAME,values,Contract.Photo._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Photo.CONTENT_URI, null);
                return affectedRows;
            }
            case PHOTO_DESCRIPTION_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.update(Contract.PhotoDescription.TABLE_NAME,values,Contract.PhotoDescription._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.PhotoDescription.CONTENT_URI, null);
                return affectedRows;
            }
            case NOTE_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.update(Contract.Note.TABLE_NAME,values,Contract.Note._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Note.CONTENT_URI, null);
                return affectedRows;
            }
            case SETTINGS_ID: {
                String id = uri.getLastPathSegment();
                String[] whereArgs = {id};
                int affectedRows = db.update(Contract.Settings.TABLE_NAME,values,Contract.Settings._ID + "=?", whereArgs);
                getContext().getContentResolver().notifyChange(Contract.Settings.CONTENT_URI, null);
                return affectedRows;
            }
            default:
                return -1;
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


}
