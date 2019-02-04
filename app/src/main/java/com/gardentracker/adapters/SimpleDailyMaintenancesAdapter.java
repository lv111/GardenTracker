package com.gardentracker.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gardentracker.DetailMaintenanceActivity;
import com.gardentracker.R;
import com.gardentracker.classes.DailyMaintenance;
import com.gardentracker.classes.Maintenance;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.ArrayList;

public class SimpleDailyMaintenancesAdapter extends ArrayAdapter<DailyMaintenance> {

    private Context context;
    private ArrayList<DailyMaintenance> data;
    private Shared shared;

    public SimpleDailyMaintenancesAdapter(@NonNull Context context, @NonNull ArrayList<DailyMaintenance> data) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_daily_maintenance_simple, parent, false);

        TextView textDay = (TextView)convertView.findViewById(R.id.textDay);
        textDay.setText(shared.longToStringDayInWeek(dailyMaintenance.getTime()));

        LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.linearLayout);
        ArrayList<Maintenance> maintenances = dailyMaintenance.getMaintenances();
        for (int i = 0; i < maintenances.size(); i++)
            linearLayout.addView(getLinearLayout(maintenances.get(i),position,i));

        return convertView;
    }

    private LinearLayout getLinearLayout(final Maintenance maintenance, final int position, final int index) {
        LinearLayout linearLayout = new LinearLayout(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_simple_without_margins, linearLayout, false);

        TextView tv = (TextView)view.findViewById(R.id.text);
        tv.setText(maintenance.getName());
        tv.setTextColor(context.getResources().getColor(R.color.colorGreyDark));
        linearLayout.addView(view);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailMaintenanceActivity.class);
                intent.putExtra("id",maintenance.getId());
                context.startActivity(intent);
            }
        });

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final Dialog dialog = shared.openSimpleDialog(context,context.getResources().getString(R.string.did_maintenance_dialog_title),
                        context.getResources().getString(R.string.did_maintenance_dialog_question),
                        context.getResources().getString(R.string.yes),context.getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateCheckInMaintenance(maintenance,position,index);
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();

                return true;
            }
        });

        return linearLayout;
    }

    private void updateCheckInMaintenance(Maintenance maintenance, final int position, final int index) {
        final long lastCheck = System.currentTimeMillis()/1000;
        final long nextCheck = lastCheck + maintenance.getIntervalInDays() * 60*60*24;
        final long changed = System.currentTimeMillis()/1000;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Maintenance.LAST_CHECK,lastCheck);
        contentValues.put(Contract.Maintenance.NEXT_CHECK,nextCheck);
        contentValues.put(Contract.Maintenance.CHANGED,changed);

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onUpdateComplete(int token, Object cookie, int result) {
                super.onUpdateComplete(token, cookie, result);
                changeData(position, index,lastCheck,nextCheck,changed);
            }
        };
        asyncQueryHandler.startUpdate(0,null, Uri.withAppendedPath(Contract.Maintenance.CONTENT_URI,String.valueOf(maintenance.getId())),contentValues,null,null);
    }

    private void changeData(int position, int index, long lastCheck, long nextCheck, long changed) {
        Maintenance oldMaintenance = data.get(position).getMaintenances().get(index);

        Maintenance newMaintenance = new Maintenance(oldMaintenance.getId(),oldMaintenance.getName(),oldMaintenance.getDescription(),lastCheck,nextCheck,oldMaintenance.getIntervalInDays(),changed);
        if(data.get(position).getMaintenances().size() == 1)
            //v dany den je len jeden maintenance, ktory vyhodime
            data.remove(position);
        else
            //v dany den je viac maintenance
            data.get(position).getMaintenances().remove(index);

        long dayLow = shared.atStartOfDay(newMaintenance.getNextCheck()*1000);
        long dayHigh = shared.atEndOfDay(newMaintenance.getNextCheck()*1000);
        long lastDayHigh = 0;
        boolean added = false;
        for(int i = 0; i < data.size(); i++) {
            DailyMaintenance dailyMaintenance = data.get(i);
            long time = dailyMaintenance.getTime()*1000;
            long actualDayLow = shared.atStartOfDay(time);
            if (dayLow <= time && dayHigh >= time) {
                dailyMaintenance.getMaintenances().add(newMaintenance);
                added = true;
                i = data.size();
            }
            else if (dayLow >= lastDayHigh && dayLow <= actualDayLow) {
                ArrayList<Maintenance> maintenances = new ArrayList<>();
                maintenances.add(newMaintenance);
                data.add(i,new DailyMaintenance(newMaintenance.getNextCheck(),maintenances));
                added = true;
                i = data.size();
            }
            lastDayHigh = shared.atEndOfDay(time);
        }
        if(!added) {
            ArrayList<Maintenance> maintenances = new ArrayList<>();
            maintenances.add(newMaintenance);
            data.add(new DailyMaintenance(newMaintenance.getNextCheck(),maintenances));
        }

        notifyDataSetChanged();
    }

    public void updateData(ArrayList<DailyMaintenance> newData) {
        data = newData;
        notifyDataSetChanged();
    }
}
