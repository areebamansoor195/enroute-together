package com.example.areebamansoor.enroutetogether;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AppClass extends MultiDexApplication {

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
