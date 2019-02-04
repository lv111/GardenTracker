package com.gardentracker.classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gardentracker.AddNewMaintenanceActivity;
import com.gardentracker.R;
import com.gardentracker.provider.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Shared {

    public Bitmap getScaledDownBitmap(Bitmap bitmap, int threshold, boolean isNecessaryToKeepOrig){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;

        if(width > height && width > threshold) {
            newWidth = threshold;
            newHeight = (int)(height * (float)newWidth/width);
        }

        if(width > height && width <= threshold)
            return bitmap;

        if(width < height && height > threshold) {
            newHeight = threshold;
            newWidth = (int)(width * (float)newHeight/height);
        }

        if(width < height && height <= threshold)
            return bitmap;

        if(width == height && width > threshold) {
            newWidth = threshold;
            newHeight = newWidth;
        }

        if(width == height && width <= threshold)
            return bitmap;

        return getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if(!isNecessaryToKeepOrig){
            bm.recycle();
        }
        return resizedBitmap;
    }

    public boolean checkPermissionPhoto(Context context) {
        int result = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionGallery(Context context) {
        int result = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionWeather(Context context) {
        int result = ContextCompat.checkSelfPermission(context, INTERNET);
        int result1 = ContextCompat.checkSelfPermission(context, ACCESS_NETWORK_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public ArrayList<DailyMaintenance> setMaintenancesArrayFromCursor(Cursor cursor) {
        ArrayList<DailyMaintenance> dailyMaintenances = new ArrayList<>();
        ArrayList<Maintenance> maintenances = new ArrayList<>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long actualDayLow = -1, actualDayHigh = -1;
            long actualDay;
            do {
                actualDay = cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.NEXT_CHECK)) * 1000;
                if (actualDayLow == -1 && actualDayHigh == -1) {
                    actualDayLow = atStartOfDay(actualDay);
                    actualDayHigh = atEndOfDay(actualDay);
                }
                if (actualDay < actualDayLow || actualDay > actualDayHigh) {
                    dailyMaintenances.add(new DailyMaintenance(actualDayLow / 1000, maintenances));
                    maintenances = new ArrayList<>();
                    actualDayLow = atStartOfDay(actualDay);
                    actualDayHigh = atEndOfDay(actualDay);
                }
                maintenances.add(new Maintenance(
                        cursor.getInt(cursor.getColumnIndex(Contract.Maintenance._ID)),
                        cursor.getString(cursor.getColumnIndex(Contract.Maintenance.NAME)),
                        cursor.getString(cursor.getColumnIndex(Contract.Maintenance.DESCRIPTION)),
                        cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.LAST_CHECK)),
                        cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.NEXT_CHECK)),
                        cursor.getInt(cursor.getColumnIndex(Contract.Maintenance.INTERVAL_IN_DAYS)),
                        cursor.getLong(cursor.getColumnIndex(Contract.Maintenance.CHANGED))));
            } while (cursor.moveToNext());
            dailyMaintenances.add(new DailyMaintenance(actualDayLow / 1000, maintenances));
            return dailyMaintenances;
        }
        else
            return null;
    }

    public Dialog openSimpleDialogEditText(Context context, String textHint, String text){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_edit_text);

        EditText editText = (EditText)dialog.findViewById(R.id.editText);
        if (textHint != null)
            editText.setHint(textHint);
        if (text != null)
            editText.setText(text);

        (dialog.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public Dialog openPickerDialog(Context context, int maxValue, int valueToSet, final String[] displayedValues){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_picker);

        NumberPicker numberPicker = (NumberPicker)dialog.findViewById(R.id.numberPicker);
        if (displayedValues != null)
            numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(valueToSet);
        numberPicker.setWrapSelectorWheel(false);

        (dialog.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public Dialog openSimpleDialog(Context context, @NonNull String title, @NonNull String question, String positiveButtonText, String negativeButtonText) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_simple);

        ((TextView)dialog.findViewById(R.id.textTitle)).setText(title);
        ((TextView)dialog.findViewById(R.id.textQuestion)).setText(question);

        if (positiveButtonText != null)
            ((Button)dialog.findViewById(R.id.buttonOk)).setText(positiveButtonText);
        if (negativeButtonText != null)
            ((Button)dialog.findViewById(R.id.buttonCancel)).setText(negativeButtonText);

        (dialog.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public long atStartOfDay(long actualDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(actualDay);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    public long atEndOfDay(long actualDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(actualDay);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
    }

    public String longToStringTime(long time) {
        Date date = new Date(time*1000);
        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        return format.format(date);
    }

    public String longToStringDateLong(long time) {
        Date date = new Date(time*1000);
        DateFormat parseFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        return parseFormat.format(date);
    }

    public long stringDateLongToLong(String string) {
        DateFormat parseFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        try {
            return parseFormat.parse(string).getTime()/1000;
        } catch (Exception e) {
            return -1;
        }
    }

    public String longToStringDateShort(long time) {
        Date date = new Date(time*1000);
        DateFormat parseFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return parseFormat.format(date);
    }

    public long stringDateShortToLong(String string) {
        DateFormat parseFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        try {
            return parseFormat.parse(string).getTime()/1000;
        } catch (Exception e) {
            return -1;
        }
    }

    public String longToStringDayInWeek(long time) {
        Date date = new Date(time*1000);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("EEEE");
        return parseFormat.format(date);
    }
}
