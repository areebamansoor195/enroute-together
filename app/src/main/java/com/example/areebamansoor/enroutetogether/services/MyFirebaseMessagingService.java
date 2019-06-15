package com.example.areebamansoor.enroutetogether.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.areebamansoor.enroutetogether.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = "FirebaseMsgService";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        Log.e(TAG, remoteMessage.getData().toString());
        sendNotification(remoteMessage.getData().toString());

    }


    private void sendNotification(String msg) {

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Job Request")
                .setContentText(msg)
                //.setSmallIcon(icon)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(/*notification id*/0, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String msg) {

       /* NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(CHANNEL_ID);

        CharSequence name = this.getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Bus On Time")
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND);

        notificationManager.createNotificationChannel(mChannel);

        notificationManager.notify(1, notification.build());*/
    }

}