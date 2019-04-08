package com.example.areebamansoor.enroutetogether.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {

    private static Firebase mInstance = null;
    public DatabaseReference mDatabase;

    private Firebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static Firebase getInstance() {
        if (mInstance == null) {
            mInstance = new Firebase();
        }

        return mInstance;
    }

}
