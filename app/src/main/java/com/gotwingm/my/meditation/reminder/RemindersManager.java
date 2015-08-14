package com.gotwingm.my.meditation.reminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.gotwingm.my.meditation.MainActivity;
import com.gotwingm.my.meditation.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class RemindersManager extends MainActivity implements View.OnClickListener {

    private static final String TABLE_NAME = "reminders";
    private static final int FULL_ALIGN_BACKGROUND_COLOR = Color.parseColor("#00000000");
    private static final String TIMING = "timing";
    private static final String DAYS = "days";
    private static final String TIME_OF_A_DAY = "time";
    private static final String SUCCESS_DIALOG = "OK";
    private static final String CANCEL_DIALOG = "cancel";
    private static final String TRUE = "1";
    private static final String FALSE = "0";
    private static final String ACTION_SEPARATOR = "_";

    private static String currentTimeOADay;

    private int currentTamingIndex;
    private View mView;
    private String[] days;
    private String[] timings;
    private String[] timesOfADay;
    private int[] dayButtonsID;
    private int[] timingButtonsID;
    private boolean[] daysPicked;
    private Calendar mCalendar;
    private Button timePickerButton;
    private RemindersDBHelper RemindersDB;
    private SQLiteDatabase db;
    private AlarmManager mAlarmManager;

    public RemindersManager() {

        RemindersDB = new RemindersDBHelper(context);
        mView = null;
        timer = 1;
        daysPicked = new boolean[7];
        mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        remindersView.findViewById(R.id.min1R).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.min5R).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.min10R).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.min20R).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.kidsR).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day1).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day2).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day3).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day4).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day5).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day6).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.day7).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.cancelRemindersButton).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.okRemindersButton).setOnClickListener(RemindersManager.this);
        remindersView.findViewById(R.id.remindersTimePickerButton).setOnClickListener(RemindersManager.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.cancelRemindersButton:
                cancelAlarms();
                break;

            case R.id.okRemindersButton:
                createAlarms();
                break;

            case R.id.remindersTimePickerButton:
                createTimePickerDialogue();
                break;

            case R.id.min1R:
                meditationTamingChooseButton(0);
                timer = 1;
                break;

            case R.id.min5R:
                meditationTamingChooseButton(1);
                timer = 5;
                break;

            case R.id.min10R:
                meditationTamingChooseButton(2);
                timer = 10;
                break;

            case R.id.min20R:
                meditationTamingChooseButton(3);
                timer = 20;
                break;

            case R.id.kidsR:
                meditationTamingChooseButton(4);
                timer = 5;
                break;

            case R.id.day1:
                daysChooseButton(0);
                break;

            case R.id.day2:
                daysChooseButton(1);
                break;

            case R.id.day3:
                daysChooseButton(2);
                break;

            case R.id.day4:
                daysChooseButton(3);
                break;

            case R.id.day5:
                daysChooseButton(4);
                break;

            case R.id.day6:
                daysChooseButton(5);
                break;

            case R.id.day7:
                daysChooseButton(6);
                break;
        }
    }

    private void cancelAlarms() {

        readRemindersFromDB();

        Log.d("###", "timings = " + Arrays.toString(timings)
                + ", days = " + Arrays.toString(days) + ", timesOfADay = " + Arrays.toString(timesOfADay));

        String action;
        Intent intent;

        for (int i = 0; i < timings.length; i++) {
            for (int j = 0; j < dayButtonsID.length; j++) {
                if ((days[i].split("")[j + 1]).equals(TRUE)) {

                    action = timings[i] + ACTION_SEPARATOR
                            + getDayOfWeekName(j + 1) + ACTION_SEPARATOR
                            + timesOfADay[i];
                    Log.d("###", " action = " + action);

                    intent = new Intent(context, RemindersReceiver.class)
                            .setAction(action);

                    mAlarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT));
                }
            }
        }

        db = RemindersDB.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        RemindersDB.onCreate(db);
        db.close();

        readRemindersFromDB();
        setChosenDays();

        if (timesOfADay[currentTamingIndex].equals(FALSE)) {
            timesOfADay[currentTamingIndex] = currentTimeOADay;
        }

        setTimeToTimePickButton();
        alertDialog(CANCEL_DIALOG);
    }

    private void createAlarms() {

        PendingIntent alarmPendingIntent;
        Intent intent;
        String action;
        int hour;
        int minute;

        Log.d("###", "timings = " + Arrays.toString(timings)
                + ", days = " + Arrays.toString(days) + ", timesOfADay = " + Arrays.toString(timesOfADay));

        for (int i = 0; i < timings.length; i++) {
            for (int j = 0; j < dayButtonsID.length; j++) {
                if ((days[i].split("")[j + 1]).equals(TRUE)) {

                    action = timings[i] + ACTION_SEPARATOR
                            + getDayOfWeekName(j + 1) + ACTION_SEPARATOR
                            + timesOfADay[i];
                    Log.d("###", " action " + action);

                    intent = new Intent(context, RemindersReceiver.class)
                            .setAction(action).putExtra(TIME, timer)
                            .putExtra(TIME, getTimingOfMeditation(timings[i]));

                    alarmPendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    hour = Integer.parseInt((timesOfADay[i].split(""))[1] + (timesOfADay[i].split(""))[2]);
                    minute = Integer.parseInt((timesOfADay[i].split(":"))[1]);

                    mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                    mCalendar.set(Calendar.MINUTE, minute);

                    mAlarmManager.setRepeating(AlarmManager.RTC,
                        mCalendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, alarmPendingIntent);
                }
            }
        }

        db = RemindersDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        int k = 0;

        while (k < 5) {
            values.put(TIMING, timings[k]);
            values.put(DAYS, days[k]);
            values.put(TIME_OF_A_DAY, timesOfADay[k]);
            db.update(TABLE_NAME, values, "_ID = ?", new String[]{"" + (k + 1)});
            values.clear();
            k++;
        }
        db.close();

        alertDialog(SUCCESS_DIALOG);
    }

    public void makeRemindersView() {

        currentTamingIndex = 0;
        days = new String[5];
        timings = new String[5];
        timesOfADay = new String[5];
        timingButtonsID = new int[] {R.id.min1R, R.id.min5R, R.id.min10R, R.id.min20R, R.id.kidsR};
        dayButtonsID = new int[] {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7};
        mCalendar = new GregorianCalendar();
        timer = 1;
        currentTimeOADay = getCorrectTime(mCalendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + getCorrectTime(mCalendar.get(Calendar.MINUTE));

        readRemindersFromDB();
        setChosenDays();

        for (int id : timingButtonsID) {
            remindersView.findViewById(id).setBackgroundColor(FULL_ALIGN_BACKGROUND_COLOR);
        }

        ((ImageView)remindersView.findViewById(R.id.remindersKidIcon)).setImageResource(kidIconId);
        remindersView.findViewById(R.id.min1R).setBackgroundResource(R.drawable.general_chosen_button_background);
        mView = remindersView.findViewById(R.id.min1R);
        timePickerButton = (Button) remindersView.findViewById(R.id.remindersTimePickerButton);

        if (timesOfADay[currentTamingIndex].equals(FALSE)) {
            timesOfADay[currentTamingIndex] = currentTimeOADay;
        }
        setTimeToTimePickButton();

        mainViewFlipper.addView(remindersView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_out));
        mainViewFlipper.showNext();
        mainViewFlipper.removeViewAt(0);
    }

    private void readRemindersFromDB() {

        Cursor cursor;

        db = RemindersDB.getWritableDatabase();
        cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int i = 0;
            int timingColIndex = cursor.getColumnIndex(TIMING);
            int daysColIndex = cursor.getColumnIndex(DAYS);
            int timeOfADayColIndex = cursor.getColumnIndex(TIME_OF_A_DAY);

            do {
                timings[i] = cursor.getString(timingColIndex);
                days[i] = cursor.getString(daysColIndex);
                timesOfADay[i] = cursor.getString(timeOfADayColIndex);
                i++;
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }
    }

    private void meditationTamingChooseButton(int tamingButtonIndex) {

        currentTamingIndex = tamingButtonIndex;
        if (timesOfADay[currentTamingIndex].equals(FALSE)) {
            timesOfADay[currentTamingIndex] = currentTimeOADay;
        }

        if (mView != null) {
            mView.setBackgroundColor(FULL_ALIGN_BACKGROUND_COLOR);
        }
        mView = remindersView.findViewById(timingButtonsID[currentTamingIndex]);
        mView.setBackgroundResource(R.drawable.general_chosen_button_background);

        setTimeToTimePickButton();
        setChosenDays();

    }

    private void setTimeToTimePickButton() {
        timePickerButton.setText(!(timesOfADay[currentTamingIndex].equals(FALSE))
                ? timesOfADay[currentTamingIndex] : currentTimeOADay);
    }

    private void setChosenDays() {

        for (int id : dayButtonsID) {
            remindersView.findViewById(id).setBackgroundResource(R.drawable.general_button_background);
        }

        for (int i = 0; i < 7; i++) {
            if ((days[currentTamingIndex].split("")[i + 1]).equals(TRUE)) {
                daysPicked[i] = true;
                remindersView.findViewById(dayButtonsID[i]).setBackgroundResource(R.drawable.general_chosen_button_background);
            } else {
                daysPicked[i] = false;
            }
        }
    }

    private void daysChooseButton(int dayIndex) {

        daysPicked[dayIndex] = !daysPicked[dayIndex];

        if (daysPicked[dayIndex]) {
            remindersView.findViewById(dayButtonsID[dayIndex]).setBackgroundResource(R.drawable.general_chosen_button_background);
        } else {
            remindersView.findViewById(dayButtonsID[dayIndex]).setBackgroundResource(R.drawable.general_button_background);
        }

        commitChosenDay();
    }

    private void commitChosenDay() {

        String string = "";

        for (boolean aDaysPicked : daysPicked) {
            string = aDaysPicked ? string + TRUE : string + FALSE;
        }

        days[currentTamingIndex] = string;
    }

    private void commitChosenTimeOfADay() {
        timesOfADay[currentTamingIndex] = getCorrectTime(mCalendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + getCorrectTime(mCalendar.get(Calendar.MINUTE));
    }

    private void createTimePickerDialogue() {

        Dialog timePickerDialog;

        timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);

                        commitChosenTimeOfADay();
                        setTimeToTimePickButton();

                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    private String getCorrectTime(int i) {

        if (i >= 0 && i <= 9) {
            return FALSE + i;
        } else return String.valueOf(i);

    }

    private String getDayOfWeekName(int dayOfWeek){

        switch (dayOfWeek) {
            case 1:
                return "SUNDAY";
            case 2:
                return "MONDAY";
            case 3:
                return "TUESDAY";
            case 4:
                return "WEDNESDAY";
            case 5:
                return "THURSDAY";
            case 6:
                return "FRIDAY";
            case 7:
                return "SATURDAY";
            default: return "";
        }
    }

    private int getTimingOfMeditation(String timing) {

        int i = 1;

        switch (timing) {
            case "1min":
                i = 1;
                break;

            case "5min":
                i = 5;
                break;

            case "10min":
                i = 10;
                break;

            case "20min":
                i = 20;
                break;

            case "kids":
                i = 5;
                break;
        }
        return i;
    }

    private void alertDialog(String action) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        switch (action) {
            case SUCCESS_DIALOG:
                builder.setTitle(context.getResources().getString(R.string.remindersSet));
                break;
            case CANCEL_DIALOG:
                builder.setTitle(context.getResources().getString(R.string.remindersCanceled));
                break;
        }

        builder.setNeutralButton(SUCCESS_DIALOG, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
