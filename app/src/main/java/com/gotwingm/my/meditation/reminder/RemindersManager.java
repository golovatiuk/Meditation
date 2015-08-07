package com.gotwingm.my.meditation.reminder;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gotwingm.my.meditation.MainActivity;
import com.gotwingm.my.meditation.R;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class RemindersManager extends MainActivity implements View.OnClickListener {

    private static final String TABLE_NAME = "reminders";
    private static final int FULL_ALIGN_BACKGROUND_COLOR = Color.parseColor("#00000000");
    private static final String ACTION = "action";

    public static int mHour;
    public static int mMinute;

    private View mView;
    private int day;
    private boolean[] daysPicked;
    private Calendar mCalendar;
    private AlarmManager mAlarmManager;
    private Button timePickerButton;
    private RemindersDBHelper RemindersDB;

    public RemindersManager() {

        RemindersDB = new RemindersDBHelper(context);
        mView = null;
        timer = 1;
        day = 0;
        daysPicked = new boolean[]{false, false, false, false, false, false, false, false};

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
                setMeditationTimerChooseButton(v);
                timer = 1;
                break;

            case R.id.min5R:
                setMeditationTimerChooseButton(v);
                timer = 5;
                break;

            case R.id.min10R:
                setMeditationTimerChooseButton(v);
                timer = 10;
                break;

            case R.id.min20R:
                setMeditationTimerChooseButton(v);
                timer = 20;
                break;

            case R.id.kidsR:
                setMeditationTimerChooseButton(v);
                break;

            case R.id.day1:
                day = 0;
                daysChooseButton(v);
                break;

            case R.id.day2:
                day = 1;
                daysChooseButton(v);
                break;

            case R.id.day3:
                day = 2;
                daysChooseButton(v);
                break;

            case R.id.day4:
                day = 3;
                daysChooseButton(v);
                break;

            case R.id.day5:
                day = 4;
                daysChooseButton(v);
                break;

            case R.id.day6:
                day = 5;
                daysChooseButton(v);
                break;

            case R.id.day7:
                day = 6;
                daysChooseButton(v);
                break;
        }
    }

    private void cancelAlarms() {

        SQLiteDatabase db;
        Cursor cursor;

        db = RemindersDB.getWritableDatabase();
        cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int actionColumnIndex = cursor.getColumnIndex(ACTION);
            do {
                mAlarmManager.cancel(PendingIntent.getBroadcast(context, 0,
                        new Intent(context, RemindersReceiver.class)
                                .setAction(cursor.getString(actionColumnIndex)),
                        PendingIntent.FLAG_CANCEL_CURRENT));
            } while (cursor.moveToNext());

            Toast.makeText(context, "Reminder(s) canceled.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACTION + " TEXT);");

    }

    public void makeRemindersView() {

        mCalendar = new GregorianCalendar();

        mHour = mCalendar.HOUR_OF_DAY;
        mMinute = mCalendar.MINUTE;

        ((ImageView)remindersView.findViewById(R.id.remindersKidIcon)).setImageResource(kidIconId);

        mainViewFlipper.addView(remindersView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_out));
        mainViewFlipper.showNext();
        timePickerButton = (Button) remindersView.findViewById(R.id.remindersTimePickerButton);
        timePickerButton.setText(makeCorrectTime(mCalendar.get(Calendar.HOUR_OF_DAY)) + ":"
                                + makeCorrectTime(mCalendar.get(Calendar.MINUTE)));
        mainViewFlipper.removeViewAt(0);

    }

    private void setMeditationTimerChooseButton(View v) {

        if (mView != null) {
            mView.setBackgroundColor(FULL_ALIGN_BACKGROUND_COLOR);
        }
        mView = v;
        v.setBackgroundResource(R.drawable.general_chosen_button_background);

    }

    private void daysChooseButton(View v) {

        daysPicked[day] = !daysPicked[day];
        if (daysPicked[day]) {
            v.setBackgroundResource(R.drawable.general_chosen_button_background);
        } else {
            v.setBackgroundResource(R.drawable.general_button_background);
        }

    }

    public void createAlarms() {

        PendingIntent alarmPendingIntent;
        SQLiteDatabase db;
        String action;
        ContentValues values;
        int dayCount = 0;

        db = RemindersDB.getWritableDatabase();
        values = new ContentValues();

        for (boolean isPicked : daysPicked) {
            if (isPicked) {
                switch (dayCount) {
                    case 0:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        break;
                    case 1:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        break;
                    case 2:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        break;
                    case 3:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        break;
                    case 4:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        break;
                    case 5:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                        break;
                    case 6:
                        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        break;
                }

                action = returnDayOfWeekName(mCalendar.get(Calendar.DAY_OF_WEEK)) + "_"
                        + mCalendar.get(Calendar.HOUR_OF_DAY) + ":"
                        + mCalendar.get(Calendar.MINUTE);

                alarmPendingIntent = PendingIntent.getBroadcast(context, 0,
                        new Intent(context, RemindersReceiver.class).setAction(action),
                        PendingIntent.FLAG_CANCEL_CURRENT);

                mAlarmManager.setRepeating(AlarmManager.RTC,
                        mCalendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, alarmPendingIntent);

                values.put(ACTION, action);
                db.insert(TABLE_NAME, null, values);
                values.clear();

                Toast.makeText(context, "Reminder created " + action, Toast.LENGTH_SHORT).show();

            }
            dayCount++;
        }
    }

    private void createTimePickerDialogue() {

        Dialog timePickerDialog;

        timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;
                        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                        mCalendar.set(Calendar.MINUTE, mMinute);

                        timePickerButton.setText(makeCorrectTime(hourOfDay) + ":" + makeCorrectTime(minute));

                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();

    }

    private String makeCorrectTime(int i) {

        if (i >= 0 && i <= 9) {
            return "0" + i;
        } else return String.valueOf(i);

    }

    private String returnDayOfWeekName(int dayOfWeek){

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

}
