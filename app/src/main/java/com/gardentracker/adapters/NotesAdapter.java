package com.gardentracker.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gardentracker.DetailNoteActivity;
import com.gardentracker.R;
import com.gardentracker.classes.Note;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<Note> {

    private Context context;
    private ArrayList<Note> data;
    private Shared shared;

    public NotesAdapter(@NonNull Context context, @NonNull ArrayList<Note> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
        this.shared = new Shared();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Note note = getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_text_with_update_time, parent, false);

        TextView textDescription = (TextView)convertView.findViewById(R.id.textDescription);
        textDescription.setText(note.getDescription());

        TextView textDate = (TextView)convertView.findViewById(R.id.textDate);
        textDate.setText(String.format(context.getResources().getString(R.string.added_date),shared.longToStringDateShort(note.getChanged())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote(note);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = shared.openSimpleDialog(context, context.getResources().getString(R.string.remove_note_dialog_title),
                        context.getResources().getString(R.string.remove_note_dialog_question),
                        context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeNote(note, position);
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

        return convertView;
    }

    private void updateNote(Note note) {
        Intent intent = new Intent(context,DetailNoteActivity.class);
        intent.putExtra("id",note.getId());
        context.startActivity(intent);
    }

    private void removeNote(Note note, final int position) {
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Toast.makeText(context,context.getResources().getString(R.string.deleted),Toast.LENGTH_SHORT).show();

                data.remove(position);
                updateData(data);
            }
        };
        asyncQueryHandler.startDelete(0,null,Uri.withAppendedPath(Contract.Note.CONTENT_URI,String.valueOf(note.getId())),null,null);
    }

    public void updateData(ArrayList<Note> newData) {
        data = newData;
        notifyDataSetChanged();
    }
}
