package com.gardentracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

import com.gardentracker.classes.Maintenance;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.Calendar;

public class DetailMaintenanceActivity extends AppCompatActivity {

    Shared shared;
    Maintenance maintenance;
    int numberOfDaysAfter = 10;
    int numberOfDaysBefore = 10;
    final String[] array = new String[numberOfDaysAfter + 1 + numberOfDaysBefore];

    View.OnClickListener openDatePickerDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getArrayToNumberPicker();
            final TextView textView = (TextView)findViewById(R.id.textStartDay);
            final Dialog dialog = shared.openPickerDialog(DetailMaintenanceActivity.this,array.length-1, (array.length-1)/2, array);

            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    int value = ((NumberPicker)dialog.findViewById(R.id.numberPicker)).getValue();
                    textView.setText(array[value]);
                    textView.setTag(shared.stringDateLongToLong(array[value]));
                }
            });

            dialog.show();
        }
    };

    View.OnClickListener openNumberPickerDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final TextView textView = (TextView)findViewById(R.id.textNotificationInterval);
            final Dialog dialog = shared.openPickerDialog(DetailMaintenanceActivity.this,30,Integer.parseInt(textView.getTag().toString()),null);
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

        setMaintenance();
    }

    private void setMaintenance() {
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",-1);
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                maintenance = new Maintenance(id,cursor.getString(cursor.getColumnIndex(Contract.Maintenance.NAME)),
                        cursor.getString(cursor.getColumnIndex(Contract.Maintenance.DESCRIPTION)),
                        cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.LAST_CHECK)),
                        cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.NEXT_CHECK)),
                        cursor.getInt(cursor.getColumnIndex(Contract.Maintenance.INTERVAL_IN_DAYS)),
                        cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.CHANGED)));
                setUI();
            }
        };
        asyncQueryHandler.startQuery(0,null,Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI,String.valueOf(id)),null,null,null,null);
    }

    private void getArrayToNumberPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 1000*60*60*24 * numberOfDaysAfter);
        for(int i = 0; i < array.length; i++) {
            array[i] = shared.longToStringDateLong(calendar.getTimeInMillis()/1000);
            calendar.setTimeInMillis(calendar.getTimeInMillis() - 1000*60*60*24);
        }
    }

    private void setUI() {
        shared = new Shared();

        ((EditText)findViewById(R.id.editMaintenanceName)).setText(maintenance.getName());
        ((EditText)findViewById(R.id.editMaintenanceDescription)).setText(maintenance.getDescription());

        TextView textView = (TextView)findViewById(R.id.textStartDay);
        textView.setOnClickListener(openDatePickerDialog);
        textView.setText(shared.longToStringDateLong(maintenance.getLastCheck()));
        textView.setTag(maintenance.getLastCheck());

        (findViewById(R.id.text3)).setOnClickListener(openDatePickerDialog);

        textView = (TextView)findViewById(R.id.textNotificationInterval);
        textView.setOnClickListener(openNumberPickerDialog);
        textView.setText(String.valueOf(maintenance.getIntervalInDays()));
        textView.setTag(maintenance.getIntervalInDays());
        (findViewById(R.id.text6)).setOnClickListener(openNumberPickerDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_edit) {
            final String name = ((EditText) findViewById(R.id.editMaintenanceName)).getText().toString();
            final String description = ((EditText) findViewById(R.id.editMaintenanceDescription)).getText().toString();
            if (name.length() == 0 || description.length() == 0)
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_form),Toast.LENGTH_LONG).show();
            else {
                final Dialog dialog = shared.openSimpleDialog(DetailMaintenanceActivity.this,getResources().getString(R.string.save_maintenance_dialog_title),
                        getResources().getString(R.string.save_maintenance_dialog_question),
                        getResources().getString(R.string.yes),getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maintenance.setName(name);
                        maintenance.setDescription(description);
                        maintenance.setLastCheck(Long.parseLong((findViewById(R.id.textStartDay)).getTag().toString()));
                        maintenance.setIntervalInDays(Integer.parseInt((findViewById(R.id.textNotificationInterval)).getTag().toString()));
                        updateMaintenance();
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
        } else if (id == R.id.action_remove) {
            final Dialog dialog = shared.openSimpleDialog(DetailMaintenanceActivity.this,getResources().getString(R.string.remove_maintenance_dialog_title),
                    getResources().getString(R.string.remove_maintenance_dialog_question),
                    getResources().getString(R.string.yes),getResources().getString(R.string.no));
            (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMaintenance();
                    dialog.dismiss();
                }
            });
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMaintenance() {
        long intervalInSeconds = maintenance.getIntervalInDays() * 24 * 3600;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Maintenance.NAME, maintenance.getName());
        contentValues.put(Contract.Maintenance.DESCRIPTION, maintenance.getDescription());
        contentValues.put(Contract.Maintenance.LAST_CHECK, maintenance.getLastCheck());
        contentValues.put(Contract.Maintenance.NEXT_CHECK, maintenance.getLastCheck() + intervalInSeconds);
        contentValues.put(Contract.Maintenance.INTERVAL_IN_DAYS, maintenance.getIntervalInDays());
        contentValues.put(Contract.Maintenance.CHANGED, System.currentTimeMillis()/1000);

        @SuppressLint("HandlerLeak") AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onUpdateComplete(int token, Object cookie, int result) {
                super.onUpdateComplete(token, cookie, result);
                Toast.makeText(DetailMaintenanceActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
                finish();
            }
        };
        queryHandler.startUpdate(0, null, Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI,String.valueOf(maintenance.getId())), contentValues,null,null);
    }

    private void deleteMaintenance() {
        @SuppressLint("HandlerLeak") AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Toast.makeText(DetailMaintenanceActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();
                finish();
            }
        };
        queryHandler.startDelete(0,null,Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI,String.valueOf(maintenance.getId())),null,null);
    }
}
