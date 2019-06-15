package com.example.areebamansoor.enroutetogether.model;

import java.io.Serializable;

public class ActivePassengers implements Serializable {
    private String pickup;
    private String dropoff;
    private String pickupLatLng;
    private String dropoffLatLng;
    private String currentLatlng;
    private String userId;
    private String fcmDeviceId;
    private String requestedSeats;
    private String timeStamp;

    public ActivePassengers() {
    }

    public String getFcmDeviceId() {
        return fcmDeviceId;
    }

    public void setFcmDeviceId(String fcmDeviceId) {
        this.fcmDeviceId = fcmDeviceId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRequestedSeats() {
        return requestedSeats;
    }

    public void setRequestedSeats(String requestedSeats) {
        this.requestedSeats = requestedSeats;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDropoff() {
        return dropoff;
    }

    public void setDropoff(String dropoff) {
        this.dropoff = dropoff;
    }

    public String getPickupLatLng() {
        return pickupLatLng;
    }

    public void setPickupLatLng(String pickupLatLng) {
        this.pickupLatLng = pickupLatLng;
    }

    public String getDropoffLatLng() {
        return dropoffLatLng;
    }

    public void setDropoffLatLng(String dropoffLatLng) {
        this.dropoffLatLng = dropoffLatLng;
    }

    public String getCurrentLatlng() {
        return currentLatlng;
    }

    public void setCurrentLatlng(String currentLatlng) {
        this.currentLatlng = currentLatlng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
