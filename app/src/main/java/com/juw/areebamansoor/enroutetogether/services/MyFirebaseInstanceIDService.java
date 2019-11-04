package com.juw.areebamansoor.enroutetogether.services;


import android.annotation.SuppressLint;
import android.util.Log;

import com.juw.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInstanceIDService";

    @SuppressLint("LongLogTag")
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferencHandler.setDeviceId(refreshedToken);
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }

}
