package com.example.areebamansoor.enroutetogether.model;

public class Vehicle {
    private String name;
    private String type;
    private String model;
    private String color;
    private int capacity;
    private String vehicleId;
    private String plateNumber;

    public Vehicle() {
    }

    public Vehicle(String name, String model, String color, int capacity, String vehicleId, String plateNumber, String type) {
        this.name = name;
        this.model = model;
        this.color = color;
        this.capacity = capacity;
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.type = type;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
