package com.example.areebamansoor.enroutetogether;

import android.app.Application;
import android.content.Context;

public class AppClass extends Application {

    public static AppClass instance;

    public static Context getCtx() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
