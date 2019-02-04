package com.gardentracker.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import static android.content.ContentResolver.SCHEME_CONTENT;

public interface Contract {
    interface Maintenance extends BaseColumns {
        String TABLE_NAME = "maintenance";
        String NAME = "name";
        String DESCRIPTION = "description";
        String LAST_CHECK = "last_check";
        String NEXT_CHECK = "next_check";
        String INTERVAL_IN_DAYS = "interval";
        String CHANGED = "changed";
        String AUTHORITY = "com.gardentracker";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME + " TEXT,"
                + DESCRIPTION + " TEXT,"
                + LAST_CHECK + " LONG,"
                + NEXT_CHECK + " LONG,"
                + INTERVAL_IN_DAYS + " INTEGER,"
                + CHANGED + " LONG"
                + ")";
    }

    interface Photo extends BaseColumns{
        String TABLE_NAME = "photo";
        String NAME = "name";
        String PHOTO_URI = "photo_uri";
        String MINIATURE = "miniature";
        String DELETED = "deleted";
        String CHANGED = "changed";
        String AUTHORITY = "com.gardentracker";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME + " TEXT,"
                + PHOTO_URI + " TEXT,"
                + MINIATURE + " BLOB,"
                + DELETED + " INTEGER,"
                + CHANGED + " LONG"
                + ")";
    }

    interface PhotoDescription extends BaseColumns{
        String TABLE_NAME = "photo_description";
        String ID_PHOTO = "id_photo";
        String DESCRIPTION = "description";
        String CHANGED = "changed";
        String AUTHORITY = "com.gardentracker";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_PHOTO + " INTEGER,"
                + DESCRIPTION + " TEXT,"
                + CHANGED + " LONG"
                + ")";
    }

    interface Note extends BaseColumns {
        String TABLE_NAME = "note";
        String DESCRIPTION = "description";
        String CHANGED = "changed";
        String AUTHORITY = "com.gardentracker";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DESCRIPTION + " TEXT,"
                + CHANGED + " LONG"
                + ")";
    }


    interface Weather extends BaseColumns {
        String TABLE_NAME = "weather";
        String CITY = "city";
        String CHANGED = "changed";
        String AUTHORITY = "com.gardentracker";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CITY + " TEXT,"
                + CHANGED + " LONG"
                + ")";
    }

    interface Settings extends BaseColumns {
        String TABLE_NAME = "settings";
        String NOTIFICATION_ON = "notification_on";
        String NOTIFICATION_TIME = "notification_time";
        String WEATHER_LANGUAGE = "weather_language";
        String WEATHER_CITY = "weather_city";
        String WEATHER_UNITS = "weather_units";
        String LANGUAGE = "language";
        String CHANGED = "changed";
        String AUTHORITY = "com.gardentracker";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NOTIFICATION_ON + " INTEGER,"
                + NOTIFICATION_TIME + " LONG,"
                + WEATHER_LANGUAGE + " TEXT,"
                + WEATHER_CITY + " TEXT,"
                + WEATHER_UNITS + " TEXT,"
                + LANGUAGE + " TEXT,"
                + CHANGED + " LONG"
                + ")";
    }
}
