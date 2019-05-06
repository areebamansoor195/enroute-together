package com.example.areebamansoor.enroutetogether.model;

import java.io.Serializable;

public class ActiveDrivers implements Serializable {
    private String source;
    private String destination;
    private String sourceLatLng;
    private String destinationLatLng;
    private String currentLatlng;
    private String vehicleId;
    private String status;
    private String id;

    public ActiveDrivers() {
    }

    public ActiveDrivers(String source, String destination, String sourceLatLng, String destinationLatLng, String currentLatlng, String vehicleId, String status, String id) {
        this.source = source;
        this.destination = destination;
        this.sourceLatLng = sourceLatLng;
        this.destinationLatLng = destinationLatLng;
        this.currentLatlng = currentLatlng;
        this.vehicleId = vehicleId;
        this.status = status;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
