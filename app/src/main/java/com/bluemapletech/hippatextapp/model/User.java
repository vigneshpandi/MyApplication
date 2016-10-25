package com.bluemapletech.hippatextapp.model;

/**
 * Created by Kumaresan on 18-10-2016.
 */

public class User {

   /* private Long userId;
    private String fullName;*/
    private String auth;
    private String chatPin;
    private String TINorEIN;
    private String companyName;
    private String empId;
    private String userName;
    private String password;
    private String role;
    private String profilePjhoto;
    private String providerNPIId;
    private String providerName;
    private String status;
    private String senderId;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getChatPin() {
        return chatPin;
    }

    public void setChatPin(String chatPin) {
        this.chatPin = chatPin;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTINorEIN() {
        return TINorEIN;
    }

    public void setTINorEIN(String TINorEIN) {
        this.TINorEIN = TINorEIN;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePjhoto() {
        return profilePjhoto;
    }

    public void setProfilePjhoto(String profilePjhoto) {
        this.profilePjhoto = profilePjhoto;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProviderNPIId() {
        return providerNPIId;
    }

    public void setProviderNPIId(String providerNPIId) {
        this.providerNPIId = providerNPIId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        return "User{" +
                "auth='" + auth + '\'' +
                ", chatPin='" + chatPin + '\'' +
                ", TINorEIN='" + TINorEIN + '\'' +
                ", companyName='" + companyName + '\'' +
                ", empId='" + empId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", profilePjhoto='" + profilePjhoto + '\'' +
                ", providerNPIId='" + providerNPIId + '\'' +
                ", providerName='" + providerName + '\'' +
                ", status='" + status + '\'' +
                ", senderId='" + senderId + '\'' +
                '}';
    }
}
