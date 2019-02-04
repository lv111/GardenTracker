package com.gardentracker.notifications;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.gardentracker.NotificationActivity;
import com.gardentracker.R;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;


public class AlarmReceiver extends BroadcastReceiver {

    Shared shared;
    LocalData localData;

    @Override
    public void onReceive(final Context context, Intent intent) {
        localData = new LocalData(context);
        if (localData.getReminderStatus()) {
            if (intent.getAction() != null && context != null) {
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.getHour(), localData.getMin());
                return;
            }

            shared = new Shared();
            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
                @Override
                protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                    super.onQueryComplete(token, cookie, cursor);

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int maintenanceCount = 0;
                        long currentTime = shared.atEndOfDay(System.currentTimeMillis()) / 1000;
                        do {
                            if (cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.NEXT_CHECK)) <= currentTime)
                                maintenanceCount++;
                        } while (cursor.moveToNext());

                        if (maintenanceCount > 0)
                            NotificationScheduler.showNotification(context, NotificationActivity.class,
                                    context.getResources().getQuantityString(R.plurals.notification_title, maintenanceCount, maintenanceCount),
                                    context.getResources().getString(R.string.notification_text));
                    }
                }
            };
            asyncQueryHandler.startQuery(0, null, Contract.Maintenance.CONTENT_URI, null, null, null, Contract.Maintenance.NEXT_CHECK);
        }
    }
}

