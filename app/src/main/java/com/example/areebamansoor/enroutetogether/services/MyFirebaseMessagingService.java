package com.example.areebamansoor.enroutetogether.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.areebamansoor.enroutetogether.R;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.areebamansoor.enroutetogether.RequestTypes.PASSENGER_REQUEST;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = "FirebaseMsgService";
    private User user;
    private ValueEventListener valueEventListener;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        Log.e(TAG, remoteMessage.getData().toString());
        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);

        if (remoteMessage.getData().size() > 0) {
            String notificationType = remoteMessage.getData().get("notification");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String passengerId = remoteMessage.getData().get("passengerId");
            Log.e(TAG, "Notification Type : " + notificationType);
            Log.e(TAG, "title : " + title);
            Log.e(TAG, "body: " + body + "");
            Log.e(TAG, "passengerId: " + passengerId + "");

            // ActivePassengers passenger = new Gson().fromJson(body, ActivePassengers.class);
            showNotification(title, body);

            if (notificationType.equalsIgnoreCase(String.valueOf(PASSENGER_REQUEST)))
                savePassengerRequest(passengerId);

        }

    }

    private void savePassengerRequest(final String passenger) {

        final DatabaseReference activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(user.getUserId());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeDriverRef.removeEventListener(valueEventListener);
                Log.e(TAG, dataSnapshot.getChildrenCount() + "");

                List<ActiveDrivers> myOfferRides = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ActiveDrivers activeDriverTemp = data.getValue(ActiveDrivers.class);
                    myOfferRides.add(activeDriverTemp);
                }

                if (myOfferRides.get(0).getPassengerRequests() == null) {
                    myOfferRides.get(0).setPassengerRequests(passenger);
                } else {
                    myOfferRides.get(0).setPassengerRequests(myOfferRides.get(0).getPassengerRequests() + "," + passenger);
                }

                activeDriverRef.setValue(myOfferRides).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activeDriverRef.removeEventListener(valueEventListener);
            }
        };
        activeDriverRef.addListenerForSingleValueEvent(valueEventListener);
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