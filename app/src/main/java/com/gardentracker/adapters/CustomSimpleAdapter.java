package com.gardentracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gardentracker.R;

import java.util.ArrayList;

public class CustomSimpleAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> data;

    public CustomSimpleAdapter(@NonNull Context context, @NonNull ArrayList<String> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String string = getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_simple, parent, false);

        ((TextView)convertView.findViewById(R.id.text)).setText(string);
        return convertView;
    }

    public void updateData(ArrayList<String> newData) {
        data = newData;
        notifyDataSetChanged();
    }
}
