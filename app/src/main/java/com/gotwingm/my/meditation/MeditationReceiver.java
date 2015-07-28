package com.gotwingm.my.meditation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MeditationReceiver extends BroadcastReceiver {

    NotificationManager mNotificationManager;

    public MeditationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Meditation Time!");
        builder.setContentText("Start Meditation!");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class).
                        putExtra("string extra", "extra data"),
                0));

        Notification notification = builder.build();

        mNotificationManager.notify(0, notification);

    }
}
