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

    public static String getBaseFare() {
        return preferences.getString(Constants.BASE_FARE, "20");
    }

    public static void setBaseFare(String baseFare) {
        editor.putString(Constants.BASE_FARE, baseFare);
        editor.apply();
    }

    public static String getPerKmFare() {
        return preferences.getString(Constants.PER_KM_FARE, "5");
    }

    public static void setPerKmFare(String perKmFare) {
        editor.putString(Constants.PER_KM_FARE, perKmFare);
        editor.apply();
    }

    public static void setIsLogin(Boolean loginFlag) {
        editor.putBoolean(Constants.LOGIN, loginFlag);
        editor.apply();
    }

    public static Boolean getLogin() {
        return preferences.getBoolean(Constants.LOGIN, false);
    }

    public static void setHasPendingBookRide(Boolean hasRide) {
        editor.putBoolean(Constants.HAS_PENDING_BOOK_RIDE, hasRide);
        editor.apply();
    }

    public static Boolean getPendingBookRide() {
        return preferences.getBoolean(Constants.HAS_PENDING_BOOK_RIDE, false);
    }


    public static void setHasPendingOfferRide(Boolean hasRide) {
        editor.putBoolean(Constants.HAS_PENDING_OFFER_RIDE, hasRide);
        editor.apply();
    }

    public static Boolean getPendingOfferRide() {
        return preferences.getBoolean(Constants.HAS_PENDING_OFFER_RIDE, false);
    }

}