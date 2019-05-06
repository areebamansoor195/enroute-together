package com.example.areebamansoor.enroutetogether.model;

public class User {
    private String userId;
    private String vehicleId;
    private String activeDriverId;
    private String name;
    private String organizationId;
    private String email;
    private String phone_number;
    private String password;
    private String gender;
    private String otp;
    private String image_url;
    private Boolean verified;
    private String latlng;

    public User() {

    }

    public User(String userId, String name, String organizationId, String email, String password, String gender, String OTP, Boolean isVerified) {
        this.userId = userId;
        this.name = name;
        this.organizationId = organizationId;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.otp = OTP;
        this.verified = isVerified;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getActiveDriverId() {
        return activeDriverId;
    }

    public void setActiveDriverId(String activeDriverId) {
        this.activeDriverId = activeDriverId;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
