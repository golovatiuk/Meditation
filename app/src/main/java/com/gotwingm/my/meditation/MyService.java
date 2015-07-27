package com.gotwingm.my.meditation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class MyService extends Service {

    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendNotification();

        return super.onStartCommand(intent, flags, startId);
    }


    void sendNotification() {

        Notification.Builder builder= new Notification.Builder(MainActivity.context);
        builder.setContentTitle("Meditation Time");
        builder.setContentText("Meditation time!");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
//        builder.setContentIntent(PendingIntent.getActivity(context, 0,
//                new Intent(context, MainActivity.class).
//                        putExtra(MainActivity.STRING_EXTRA, "extra data").
//                        putExtra(MainActivity.TIME_EXTRA, intent.getIntExtra(MainActivity.TIME_EXTRA, 1)),
//                0));

        Notification notification = builder.build();

        mNotificationManager.notify(0, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
