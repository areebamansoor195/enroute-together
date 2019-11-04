package com.juw.areebamansoor.enroutetogether.model;

public class Register {
    private String userId;
    private String name;
    private String organizationId;
    private String email;
    private String password;
    private String gender;
    private String otp;
    private Boolean verified;

    public Register() {

    }

    public Register(String userId, String name, String organizationId, String email, String password, String gender, String OTP, Boolean isVerified) {
        this.userId = userId;
        this.name = name;
        this.organizationId = organizationId;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.otp = OTP;
        this.verified = isVerified;
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
