package com.gardentracker.adapters;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gardentracker.R;
import com.gardentracker.classes.DailyMaintenance;
import com.gardentracker.classes.Maintenance;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.ArrayList;

public class TodayMaintenancesToDoAdapter extends ArrayAdapter<DailyMaintenance> {

    private Context context;
    private ArrayList<DailyMaintenance> data;
    private Shared shared;

    public TodayMaintenancesToDoAdapter(@NonNull Context context, @NonNull ArrayList<DailyMaintenance> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
        this.shared = new Shared();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DailyMaintenance dailyMaintenance = getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_daily_maintenance, parent, false);

        TextView textDate = (TextView)convertView.findViewById(R.id.textDate);
        textDate.setText(shared.longToStringDateShort(dailyMaintenance.getTime()));

        TextView textDay = (TextView)convertView.findViewById(R.id.textDay);
        textDay.setText(shared.longToStringDayInWeek(dailyMaintenance.getTime()));

        LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.linearLayout);
        ArrayList<Maintenance> maintenances = dailyMaintenance.getMaintenances();
        for (int i = 0; i < maintenances.size(); i++) {
            final Maintenance maintenance = maintenances.get(i);
            LinearLayout linearLayoutMaintenance = getLinearLayout(maintenance);
            linearLayout.addView(linearLayoutMaintenance);
        }

        return convertView;
    }

    private LinearLayout getLinearLayout(final Maintenance maintenance) {
        LinearLayout linearLayout = new LinearLayout(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_today_maintenance_to_do, linearLayout, false);

        ((TextView) view.findViewById(R.id.textName)).setText(maintenance.getName());
        ((TextView) view.findViewById(R.id.textDescription)).setText(maintenance.getDescription());
        (view.findViewById(R.id.checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()) {
                    long lastCheck = System.currentTimeMillis()/1000;
                    long nextCheck = lastCheck + 24*60*60*maintenance.getIntervalInDays();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Maintenance.LAST_CHECK,lastCheck);
                    contentValues.put(Contract.Maintenance.NEXT_CHECK,nextCheck);
                    contentValues.put(Contract.Maintenance.CHANGED,lastCheck);

                    @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {};
                    asyncQueryHandler.startUpdate(0,null,Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI,String.valueOf(maintenance.getId())),
                            contentValues,null,null);
                }
                else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Maintenance.LAST_CHECK,maintenance.getLastCheck());
                    contentValues.put(Contract.Maintenance.NEXT_CHECK,maintenance.getNextCheck());
                    contentValues.put(Contract.Maintenance.CHANGED,System.currentTimeMillis()/1000);

                    @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {};
                    asyncQueryHandler.startUpdate(0,null,Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI,String.valueOf(maintenance.getId())),
                            contentValues,null,null);
                }
            }
        });
        linearLayout.addView(view);

        return linearLayout;
    }

    public void updateData(ArrayList<DailyMaintenance> newData) {
        data = newData;
        notifyDataSetChanged();
    }
}
