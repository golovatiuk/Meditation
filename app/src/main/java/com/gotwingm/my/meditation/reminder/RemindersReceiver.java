package com.gotwingm.my.meditation.reminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gotwingm.my.meditation.MainActivity;
import com.gotwingm.my.meditation.R;

public class RemindersReceiver extends BroadcastReceiver {

    private static final String TIME = "time";
    private static final String STRING_EXTRA = "string extra";

    NotificationManager mNotificationManager;
    int time;

    public RemindersReceiver() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        time = intent.getIntExtra(TIME, 1);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Meditation Time!");
        builder.setContentText("Start Meditation!");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class).
                        putExtra(STRING_EXTRA, "extra data").putExtra(TIME, time),
                0));
        Notification notification = builder.build();
        mNotificationManager.notify(0, notification);

    }
}
