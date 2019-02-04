package com.gardentracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.Calendar;

public class AddNewMaintenanceActivity extends AppCompatActivity {

    Shared shared;
    int numberOfDaysAfter = 10;
    int numberOfDaysBefore = 10;
    final String[] array = new String[numberOfDaysAfter + 1 + numberOfDaysBefore];
    int notificationInterval = 3;

    View.OnClickListener openDatePickerDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getArrayToNumberPicker();
            final TextView textView = (TextView)findViewById(R.id.textStartDay);
            final Dialog dialog = shared.openPickerDialog(AddNewMaintenanceActivity.this,array.length-1, (array.length-1)/2, array);

            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    int value = ((NumberPicker)dialog.findViewById(R.id.numberPicker)).getValue();
                    textView.setText(array[value]);
                    textView.setTag(shared.stringDateShortToLong(array[value]));
                }
            });

            dialog.show();
        }
    };

    View.OnClickListener openNumberPickerDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final TextView textView = (TextView)findViewById(R.id.textNotificationInterval);
            final Dialog dialog = shared.openPickerDialog(AddNewMaintenanceActivity.this,30,Integer.parseInt(textView.getTag().toString()),null);
            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    int value = ((NumberPicker)dialog.findViewById(R.id.numberPicker)).getValue();
                    textView.setText(String.valueOf(value));
                    textView.setTag(value);
                }
            });
            dialog.show();
            ((NumberPicker)dialog.findViewById(R.id.numberPicker)).setValue(notificationInterval);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_maintenance);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUI();
    }

    private void getArrayToNumberPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 1000*60*60*24 * numberOfDaysAfter);
        for(int i = 0; i < array.length; i++) {
            array[i] = shared.longToStringDateShort(calendar.getTimeInMillis()/1000);
            calendar.setTimeInMillis(calendar.getTimeInMillis() - 1000*60*60*24);
        }
    }

    private void setUI() {
        shared = new Shared();

        TextView textView = (TextView)findViewById(R.id.textStartDay);
        textView.setOnClickListener(openDatePickerDialog);
        long currentTimeSeconds = System.currentTimeMillis()/1000;
        textView.setText(shared.longToStringDateShort(currentTimeSeconds));
        textView.setTag(currentTimeSeconds);

        (findViewById(R.id.text3)).setOnClickListener(openDatePickerDialog);

        textView = (TextView)findViewById(R.id.textNotificationInterval);
        textView.setOnClickListener(openNumberPickerDialog);
        textView.setText(String.valueOf(notificationInterval));
        textView.setTag(notificationInterval);
        (findViewById(R.id.text6)).setOnClickListener(openNumberPickerDialog);
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
            final String name = ((EditText) findViewById(R.id.editMaintenanceName)).getText().toString();
            final String description = ((EditText) findViewById(R.id.editMaintenanceDescription)).getText().toString();
            final long startDate = Long.parseLong((findViewById(R.id.textStartDay)).getTag().toString());
            final int interval = Integer.parseInt((findViewById(R.id.textNotificationInterval)).getTag().toString());

            if (name.length() == 0 || description.length() == 0)
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_form),Toast.LENGTH_LONG).show();
            else {
                final Dialog dialog = shared.openSimpleDialog(AddNewMaintenanceActivity.this,getResources().getString(R.string.add_maintenance_dialog_title),
                        getResources().getString(R.string.add_maintenance_dialog_question),
                        getResources().getString(R.string.yes),getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNewMaintenance(name, description, startDate, interval);
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
            }
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(AddNewMaintenanceActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewMaintenance(String name, String description, long startDate, int interval) {
        long intervalInSeconds = interval * 24 * 3600;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Maintenance.NAME, name);
        contentValues.put(Contract.Maintenance.DESCRIPTION, description);
        contentValues.put(Contract.Maintenance.LAST_CHECK, startDate);
        contentValues.put(Contract.Maintenance.NEXT_CHECK, startDate + intervalInSeconds);
        contentValues.put(Contract.Maintenance.INTERVAL_IN_DAYS, interval);
        contentValues.put(Contract.Maintenance.CHANGED, System.currentTimeMillis()/1000);
        @SuppressLint("HandlerLeak") AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Toast.makeText(AddNewMaintenanceActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
                finish();
            }
        };
        queryHandler.startInsert(0, null, Contract.Maintenance.CONTENT_URI, contentValues);
    }
}
