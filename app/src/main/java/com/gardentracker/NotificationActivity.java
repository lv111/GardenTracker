package com.gardentracker;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gardentracker.adapters.TodayMaintenancesToDoAdapter;
import com.gardentracker.classes.DailyMaintenance;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    Shared shared;
    ArrayList<DailyMaintenance> dailyMaintenances;
    ListView listView;
    TodayMaintenancesToDoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUI();
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
            Toast.makeText(NotificationActivity.this,getResources().getString(R.string.saved),Toast.LENGTH_SHORT).show();
            finish();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(NotificationActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUI() {
        shared = new Shared();

        String selection = Contract.Maintenance.NEXT_CHECK + "<=?";
        String[] selectionArgs = new String[] {String.valueOf( shared.atEndOfDay(System.currentTimeMillis())/1000 )};
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor != null && cursor.getCount() > 0) {
                    dailyMaintenances = shared.setMaintenancesArrayFromCursor(cursor);

                    listView =  (ListView)findViewById(R.id.listView);
                    adapter = new TodayMaintenancesToDoAdapter(NotificationActivity.this,dailyMaintenances);
                    listView.setAdapter(adapter);
                }
            }
        };
        asyncQueryHandler.startQuery(0,null,Contract.Maintenance.CONTENT_URI,null,selection,selectionArgs,null);
    }
}
