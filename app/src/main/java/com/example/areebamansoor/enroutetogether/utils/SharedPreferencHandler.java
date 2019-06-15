package com.example.areebamansoor.enroutetogether.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.areebamansoor.enroutetogether.AppClass;


public class SharedPreferencHandler {

    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AppClass.getCtx());
    private static SharedPreferences.Editor editor = preferences.edit();


    public static String getUser() {
        return preferences.getString(Constants.USER_OBJECT, "");
    }

    public static void setUser(String user) {
        editor.putString(Constants.USER_OBJECT, user);
        editor.apply();
    }

    public static String getDeviceId() {
        return preferences.getString(Constants.DEVICE_ID, "");
    }

    public static void setDeviceId(String deviceId) {
        editor.putString(Constants.DEVICE_ID, deviceId);
        editor.apply();
    }

    public static String getVehicle() {
        return preferences.getString(Constants.VEHICLE_OBJECT, "");
    }

    public static void setVehicle(String vehicle) {
        editor.putString(Constants.VEHICLE_OBJECT, vehicle);
        editor.apply();
    }

    public static void setIsLogin(Boolean loginFlag) {
        editor.putBoolean(Constants.LOGIN, loginFlag);
        editor.apply();
    }

    public static Boolean getLogin() {
        return preferences.getBoolean(Constants.LOGIN, false);
    }

}