package com.example.areebamansoor.enroutetogether.model;

import java.io.Serializable;

public class ActivePassengers implements Serializable {
    private String pickup;
    private String dropoff;
    private String pickupLatLng;
    private String dropoffLatLng;
    private String currentLatlng;
    private String userId;

    public ActivePassengers() {
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
