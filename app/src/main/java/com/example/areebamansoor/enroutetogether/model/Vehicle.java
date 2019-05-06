package com.example.areebamansoor.enroutetogether.model;

public class Vehicle {
    private String maker;
    private String model;
    private String color;
    private int capacity;
    private String vehicleId;

    public Vehicle() {
    }

    public Vehicle(String maker, String model, String color, int capacity, String vehicleId) {
        this.maker = maker;
        this.model = model;
        this.color = color;
        this.capacity = capacity;
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }


    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
