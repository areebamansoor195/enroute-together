package com.example.areebamansoor.enroutetogether.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {

    private static Firebase mInstance = null;
    public DatabaseReference mDatabase;
    public StorageReference storageReference;
    FirebaseStorage storage;

    private Firebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public static Firebase getInstance() {
        if (mInstance == null) {
            mInstance = new Firebase();
        }

        return mInstance;
    }

}
