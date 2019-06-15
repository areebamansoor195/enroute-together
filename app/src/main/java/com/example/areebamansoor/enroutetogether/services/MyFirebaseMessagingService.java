package com.example.areebamansoor.enroutetogether.services;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

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

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, remoteMessage.getData().toString());


    }


    private void sendNotification(String msg) {

       /* mediaPlayer = MediaPlayer.create(this, R.raw.notification);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();

        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Bus On Time")
                .setWhen(5000)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        //notiy notification manager about new notification
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String msg) {

     /*   mediaPlayer = MediaPlayer.create(this, R.raw.notification);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
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