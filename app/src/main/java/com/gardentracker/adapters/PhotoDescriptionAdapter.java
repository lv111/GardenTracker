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
import com.gardentracker.classes.PhotoDescription;
import com.gardentracker.classes.Shared;

import java.util.ArrayList;

public class PhotoDescriptionAdapter extends ArrayAdapter<PhotoDescription> {

    private Context context;
    private ArrayList<PhotoDescription> data;
    private Shared shared;

    public PhotoDescriptionAdapter(@NonNull Context context, @NonNull ArrayList<PhotoDescription> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
        this.shared = new Shared();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PhotoDescription photoDescription = getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_text_with_update_time, parent, false);

        ((TextView)convertView.findViewById(R.id.textDescription)).setText(photoDescription.getDescription());
        ((TextView)convertView.findViewById(R.id.textDate)).setText(String.format(context.getResources().getString(R.string.updated_date),
                shared.longToStringDateShort(photoDescription.getChanged())));
        return convertView;
    }

    public void updateData(ArrayList<PhotoDescription> newData) {
        data = newData;
        notifyDataSetChanged();
    }
}
