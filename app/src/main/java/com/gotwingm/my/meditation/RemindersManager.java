package com.gotwingm.my.meditation;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class RemindersManager extends MainActivity implements View.OnClickListener {

    private View mView;
    private static int timer;

    AlarmManager mAlarmManager;
    Button timePickerButton;

    public static int mHour;
    public static int mMinute;

    private int day;
    private boolean[] daysPicked;

    private Calendar mCalendar;

    RemindersManager() {

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

        switch (v.getId())
        {

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

    }

    public void makeRemindersView() {

        mCalendar = new GregorianCalendar();

        mHour = mCalendar.HOUR_OF_DAY;
        mMinute = mCalendar.MINUTE;

        mainViewFlipper.addView(remindersView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_out));
        mainViewFlipper.showNext();

        timePickerButton = (Button) remindersView.findViewById(R.id.remindersTimePickerButton);
        timePickerButton.setText(makeTime(mCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + makeTime(mCalendar.get(Calendar.MINUTE)));

        mainViewFlipper.removeViewAt(0);

    }

    private void setMeditationTimerChooseButton(View v) {

        if (mView != null) {
            mView.setBackgroundColor(Color.parseColor("#00000000"));
        }
        mView = v;
        v.setBackgroundResource(R.drawable.main_drawer_chosen_button_background);

    }

    private void daysChooseButton(View v) {

        daysPicked[day] = !daysPicked[day];
        if (daysPicked[day]) {

            v.setBackgroundResource(R.drawable.main_drawer_chosen_button_background);

        } else {

            v.setBackgroundResource(R.drawable.main_drawer_button_background);

        }

    }

    public void createAlarms() {

        int dayCount = 0;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(context, MeditationReceiver.class), 0);

        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);

        mAlarmManager.set(AlarmManager.RTC, mCalendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(context, "Reminder created", Toast.LENGTH_SHORT).show();

//        for (boolean isPicked : daysPicked) {
//
//            if (isPicked) {
//
//                switch (dayCount)
//                {
//                    case 0:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//                        break;
//                    case 1:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
//                        break;
//                    case 2:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
//                        break;
//                    case 3:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
//                        break;
//                    case 4:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
//                        break;
//                    case 5:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//                        break;
//                    case 6:
//                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//                        break;
//                }
//
//                mAlarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
//
//            }
//
//            dayCount++;
//
//        }

    }

    private void createTimePickerDialogue() {

        Dialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        timePickerButton.setText(makeTime(hourOfDay) + ":" + makeTime(minute));

                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();

    }

    private String makeTime(int i) {

        if (i >= 0 && i <= 9) {

            return "0" + i;

        } else return String.valueOf(i);

    }

}
