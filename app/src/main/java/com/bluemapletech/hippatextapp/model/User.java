package com.bluemapletech.hippatextapp.model;

/**
 * Created by Kumaresan on 18-10-2016.
 */

public class User {

    private Long userId;
    private String fullName;
    private String empId;
    private String role;
    private String userName;
    private String password;
    private String TINorEIN;
    private String companyName;
    private String auth;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getTINorEIN() {
        return TINorEIN;
    }

    public void setTINorEIN(String TINorEIN) {
        this.TINorEIN = TINorEIN;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", empId='" + empId + '\'' +
                ", role='" + role + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", TINorEIN='" + TINorEIN + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
