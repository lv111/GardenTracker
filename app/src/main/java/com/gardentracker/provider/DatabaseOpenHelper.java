package com.gardentracker.provider;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "garden_tracker";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.Maintenance.CREATE_TABLE);
        db.execSQL(Contract.Photo.CREATE_TABLE);
        db.execSQL(Contract.PhotoDescription.CREATE_TABLE);
        db.execSQL(Contract.Note.CREATE_TABLE);
        db.execSQL(Contract.Weather.CREATE_TABLE);
        db.execSQL(Contract.Settings.CREATE_TABLE);

        insertSettings(db);
    }

    private void insertSettings(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Settings.NOTIFICATION_ON,1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        contentValues.put(Contract.Settings.NOTIFICATION_TIME,calendar.getTimeInMillis()/1000);

        String localLanguage = Locale.getDefault().getLanguage();
        //Arabic - ar, Bulgarian - bg, Catalan - ca, Czech - cz, German - de, Greek - el, English - en, Persian (Farsi) - fa, Finnish - fi, French - fr, Galician - gl, Croatian - hr, Hungarian - hu, Italian - it, Japanese - ja, Korean - kr, Latvian - la, Lithuanian - lt, Macedonian - mk, Dutch - nl, Polish - pl, Portuguese - pt, Romanian - ro, Russian - ru, Swedish - se, Slovak - sk, Slovenian - sl, Spanish - es, Turkish - tr, Ukrainian - ua, Vietnamese - vi, Chinese Simplified - zh_cn, Chinese Traditional - zh_tw
        if (!localLanguage.equals("ar") && !localLanguage.equals("bg") && !localLanguage.equals("ca") && !localLanguage.equals("cz") && !localLanguage.equals("de")
                && !localLanguage.equals("el") && !localLanguage.equals("en") && !localLanguage.equals("fa") && !localLanguage.equals("fi") && !localLanguage.equals("fr")
                && !localLanguage.equals("gl") && !localLanguage.equals("hr") && !localLanguage.equals("hu") && !localLanguage.equals("it") && !localLanguage.equals("ja")
                && !localLanguage.equals("kr") && !localLanguage.equals("la") && !localLanguage.equals("lt") && !localLanguage.equals("mk") && !localLanguage.equals("nl")
                && !localLanguage.equals("pl") && !localLanguage.equals("pt") && !localLanguage.equals("ro") && !localLanguage.equals("ru") && !localLanguage.equals("se")
                && !localLanguage.equals("sk") && !localLanguage.equals("sl") && !localLanguage.equals("es") && !localLanguage.equals("tr") && !localLanguage.equals("ua")
                && !localLanguage.equals("vi") && !localLanguage.equals("zh_cn") && !localLanguage.equals("zh_tw"))
            localLanguage = "en";
        contentValues.put(Contract.Settings.WEATHER_LANGUAGE, localLanguage);
        contentValues.put(Contract.Settings.WEATHER_CITY,"");
        contentValues.put(Contract.Settings.WEATHER_UNITS,"celsius");
        contentValues.put(Contract.Settings.CHANGED,System.currentTimeMillis());
        db.insert(Contract.Settings.TABLE_NAME, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // upgrade is not supported
    }

}