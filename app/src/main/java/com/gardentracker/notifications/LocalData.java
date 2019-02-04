package com.gardentracker.notifications;

import android.content.Context;
import android.content.SharedPreferences;


public class LocalData {

    private static final String APP_SHARED_PREFS = "Preferences";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus = "reminderStatus";
    private static final String hour = "hour";
    private static final String min = "min";

    public LocalData(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.apply();
    }

    public boolean getReminderStatus() {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setReminderStatus(boolean status) {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }

    public int getHour() {
        return appSharedPrefs.getInt(hour, 8);
    }

    public void setHour(int h) {
        prefsEditor.putInt(hour, h);
        prefsEditor.commit();
    }

    public int getMin() {
        return appSharedPrefs.getInt(min, 0);
    }

    public void setMin(int m) {
        prefsEditor.putInt(min, m);
        prefsEditor.commit();
    }

    public void reset() {
        prefsEditor.clear();
        prefsEditor.commit();
    }
}
