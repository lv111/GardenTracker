package com.gardentracker.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.gardentracker.DetailPhotoActivity;
import com.gardentracker.R;
import com.gardentracker.classes.Photo;
import com.gardentracker.classes.Shared;
import com.gardentracker.provider.Contract;

import java.util.ArrayList;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder> {

    private ArrayList<Photo> data;
    private Context context;
    private Shared shared;

    public PhotoGalleryAdapter(Context context, ArrayList<Photo> data) {
        this.data = data;
        this.context = context;
        this.shared = new Shared();
    }

    @Override
    public PhotoGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo_gallery, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoGalleryAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final Photo photo = data.get(position);
        viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(photo.getMiniature(),0,photo.getMiniature().length));
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPhotoActivity.class);
                intent.putExtra("id",photo.getId());
                context.startActivity(intent);
            }
        });
        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = shared.openSimpleDialog(context,context.getResources().getString(R.string.remove_photo_dialog_title),
                        context.getResources().getString(R.string.remove_photo_dialog_question),
                        context.getResources().getString(R.string.yes),context.getResources().getString(R.string.no));
                (dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePhoto(position);
                        data.remove(position);
                        notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();

                return false;
            }
        });
    }

    public void deletePhoto(final int position) {
        int id = data.get(position).getId();
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                Toast.makeText(context,context.getResources().getString(R.string.deleted),Toast.LENGTH_SHORT).show();
            }
        };
        asyncQueryHandler.startDelete(0,null, Uri.withAppendedPath(Contract.Photo.CONTENT_URI,String.valueOf(id)),null,null);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.imageView);
        }
    }
}
