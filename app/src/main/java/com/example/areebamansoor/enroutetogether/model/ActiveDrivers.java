package com.example.areebamansoor.enroutetogether.model;

import com.google.gson.Gson;

import java.io.Serializable;

public class ActiveDrivers implements Serializable {
    private String source;
    private String destination;
    private String sourceLatLng;
    private String driverRoute;
    private String destinationLatLng;
    private String currentLatlng;
    private String timeStamp;
    private String availableSeats;
    private String driverDetails;
    private String vehicleDetails;

    public ActiveDrivers() {
    }

    public User getDriverDetails() {
        return new Gson().fromJson(driverDetails, User.class);
    }

    public void setDriverDetails(User driverDetails) {
        this.driverDetails = new Gson().toJson(driverDetails);
    }

    public Vehicle getVehicleDetails() {
        return new Gson().fromJson(vehicleDetails, Vehicle.class);
    }

    public void setVehicleDetails(Vehicle vehicleDetails) {
        this.vehicleDetails = new Gson().toJson(vehicleDetails);
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDriverRoute() {
        return driverRoute;
    }

    public void setDriverRoute(String driverRoute) {
        this.driverRoute = driverRoute;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSourceLatLng() {
        return sourceLatLng;
    }

    public void setSourceLatLng(String sourceLatLng) {
        this.sourceLatLng = sourceLatLng;
    }

    public String getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setDestinationLatLng(String destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }

    public String getCurrentLatlng() {
        return currentLatlng;
    }

    public void setCurrentLatlng(String currentLatlng) {
        this.currentLatlng = currentLatlng;
    }


}
