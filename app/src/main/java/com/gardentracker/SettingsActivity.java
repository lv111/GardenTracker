package com.gardentracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.gardentracker.classes.Settings;
import com.gardentracker.classes.Shared;
import com.gardentracker.notifications.AlarmReceiver;
import com.gardentracker.notifications.LocalData;
import com.gardentracker.notifications.NotificationScheduler;
import com.gardentracker.provider.Contract;

import java.util.Calendar;


public class SettingsActivity extends AppCompatActivity {

    Settings settings;
    Shared shared;
    LocalData localData;
    ClipboardManager myClipboard;

    View.OnClickListener openDialogNotificationTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final long[] array = getArrayToNumberPicker();
            int positionToSet = getPositionByActualTime(array);
            final String[] stringArray = getArrayOfTimesFromLongArray(array);
            final TextView textView = (TextView)findViewById(R.id.textNotificationTime);
            final Dialog dialog = shared.openPickerDialog(SettingsActivity.this,stringArray.length-1, positionToSet, stringArray);
            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    int value = ((NumberPicker)dialog.findViewById(R.id.numberPicker)).getValue();
                    long notificationTime = array[value];
                    textView.setText(stringArray[value]);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Settings.NOTIFICATION_TIME,notificationTime);
                    contentValues.put(Contract.Settings.CHANGED,System.currentTimeMillis()/1000);
                    @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                    asyncQueryHandler.startUpdate(0,null, Uri.withAppendedPath(Contract.Settings.CONTENT_URI,String.valueOf(settings.getId())),contentValues,null,null);

                    NotificationScheduler.cancelReminder(SettingsActivity.this,AlarmReceiver.class);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(notificationTime * 1000);

                    localData = new LocalData(getApplicationContext());
                    myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    localData.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    localData.setMin(calendar.get(Calendar.MINUTE));

                    NotificationScheduler.setReminder(SettingsActivity.this, AlarmReceiver.class, localData.getHour(), localData.getMin());
                }
            });
            dialog.show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUI();
    }

    private void setEnabled() {
        TextView textView = (TextView)findViewById(R.id.textNotificationTime);
        if (settings.getNotificationOn() == 1)
            textView.setTextColor(getResources().getColor(R.color.colorBlack));
        else
            textView.setTextColor(getResources().getColor(R.color.colorGreyMedium));
        textView.setClickable(settings.getNotificationOn() == 1);

        textView = (TextView)findViewById(R.id.text3);
        if (settings.getNotificationOn() == 1)
            textView.setTextColor(getResources().getColor(R.color.colorBlack));
        else
            textView.setTextColor(getResources().getColor(R.color.colorGreyLight));
        textView.setClickable(settings.getNotificationOn() == 1);

        if (settings.getNotificationOn() == 1) {
            ((TextView) findViewById(R.id.text4)).setTextColor(getResources().getColor(R.color.colorBlack));
            ((TextView) findViewById(R.id.text5)).setTextColor(getResources().getColor(R.color.colorGreyMedium));
        }
        else {
            ((TextView) findViewById(R.id.text4)).setTextColor(getResources().getColor(R.color.colorGreyLight));
            ((TextView) findViewById(R.id.text5)).setTextColor(getResources().getColor(R.color.colorGreyLight));
        }
    }

    private void setUI() {
        shared = new Shared();
        localData = new LocalData(SettingsActivity.this);
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                do {

                    settings = new Settings(cursor.getInt(cursor.getColumnIndex(Contract.Settings._ID)),
                            cursor.getInt(cursor.getColumnIndex(Contract.Settings.NOTIFICATION_ON)),
                            cursor.getLong(cursor.getColumnIndex(Contract.Settings.NOTIFICATION_TIME)),
                            cursor.getString(cursor.getColumnIndex(Contract.Settings.WEATHER_LANGUAGE)),
                            cursor.getString(cursor.getColumnIndex(Contract.Settings.WEATHER_CITY)),
                            cursor.getString(cursor.getColumnIndex(Contract.Settings.WEATHER_UNITS)),
                            cursor.getInt(cursor.getColumnIndex(Contract.Settings.CHANGED)));
                    if (settings.getNotificationOn() == 0) {
                        localData.setReminderStatus(false);
                        setEnabled();
                    }
                    else
                        localData.setReminderStatus(true);
                    ((Switch)findViewById(R.id.switchNotification)).setChecked(settings.getNotificationOn() == 1);

                    ((TextView)findViewById(R.id.textNotificationTime)).setText(shared.longToStringTime(settings.getNotificationTime()));
                }while (cursor.moveToNext());
            }
        };
        asyncQueryHandler.startQuery(0,null,Contract.Settings.CONTENT_URI,null,null,null,null);

        ((Switch)findViewById(R.id.switchNotification)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.setNotificationOn(1);
                    localData.setReminderStatus(true);
                }
                else {
                    settings.setNotificationOn(0);
                    localData.setReminderStatus(false);
                }
                setEnabled();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Settings.NOTIFICATION_ON,isChecked);
                contentValues.put(Contract.Settings.CHANGED,System.currentTimeMillis()/1000);

                @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                asyncQueryHandler.startUpdate(0,null, Uri.withAppendedPath(Contract.Settings.CONTENT_URI,String.valueOf(settings.getId())),contentValues,null,null);
            }
        });

        (findViewById(R.id.textNotificationTime)).setOnClickListener(openDialogNotificationTime);
        (findViewById(R.id.text3)).setOnClickListener(openDialogNotificationTime);
    }

    private String[] getArrayOfTimesFromLongArray(long[] array) {
        String[] stringArray = new String[array.length];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = shared.longToStringTime(array[i]);
        }
        return stringArray;
    }

    private long[] getArrayToNumberPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        long time = calendar.getTimeInMillis()/1000;
        int intervalInMinutes = 1;
        int added = intervalInMinutes*60;
        int length = (24*60*60)/added;
        long[] array = new long[length];
        for (int i = 0; i < array.length; i++, time += added) {
            array[i] = time;
        }
        return array;
    }

    private int getPositionByActualTime(long[] array) {
        long actualTime = System.currentTimeMillis()/1000;
        for(int i = 0; i < array.length - 2; i++) {
            if (actualTime >= array[i] && actualTime < array[i+1])
                return i;
        }
        return -1;
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
            Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
