package com.gardentracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gardentracker.R;
import com.gardentracker.classes.DayWeather;
import com.gardentracker.classes.Shared;

import java.util.ArrayList;

public class WeatherAdapter extends ArrayAdapter<DayWeather> {

    private ArrayList<DayWeather> data;
    private Shared shared;
    private Context context;

    public WeatherAdapter(@NonNull Context context, @NonNull ArrayList<DayWeather> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
        shared = new Shared();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DayWeather dayWeather = getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false);

        ((TextView)convertView.findViewById(R.id.textDay)).setText(shared.longToStringDayInWeek(dayWeather.getTime()));
        ((TextView)convertView.findViewById(R.id.textDate)).setText(shared.longToStringDateShort(dayWeather.getTime()));

        ((TextView)convertView.findViewById(R.id.textTemperature)).setText(
                String.format(context.getResources().getString(R.string.temperature_in_celsius),(int)dayWeather.getMaxTemperature(),(int)dayWeather.getMinTemperature()));
        ((ImageView)convertView.findViewById(R.id.imageViewIcon)).setImageResource(
                context.getResources().getIdentifier("icon_"+dayWeather.getIcon(),"drawable",context.getPackageName()));

        return convertView;
    }

    public void updateData(ArrayList<DayWeather> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }
}
