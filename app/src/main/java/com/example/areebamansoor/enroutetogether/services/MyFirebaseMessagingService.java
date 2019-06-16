package com.example.areebamansoor.enroutetogether.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
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

        if (remoteMessage.getData().size() > 0) {
            String notificationType = remoteMessage.getData().get("notification");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            Log.e(TAG, "Notification Type : " + notificationType);
            Log.e(TAG, "title : " + title);
            Log.e(TAG, "body: " + body);
            showNotification(title, body);

        }

    }


    private void showNotification(String title, String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String CHANNEL_ID = "my_channel_01";


            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(CHANNEL_ID);

            CharSequence name = this.getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_SOUND);

            notificationManager.createNotificationChannel(mChannel);

            notificationManager.notify(1, notification.build());
            return;
        }

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        notificationManager.notify(1, alamNotificationBuilder.build());
    }


}