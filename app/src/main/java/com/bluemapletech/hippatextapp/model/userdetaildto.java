package com.bluemapletech.hippatextapp.model;

/**
 * Created by BlueMaple on 1/5/2017.
 */

public class UserDetailDto {

    private String loggedINEmail;
    private String loggedINChatPin;
    private String role_val_det;

    public String getLoggedINEmail() {
        return loggedINEmail;
    }

    public void setLoggedINEmail(String loggedINEmail) {
        this.loggedINEmail = loggedINEmail;
    }

    public String getLoggedINChatPin() {
        return loggedINChatPin;
    }

    public void setLoggedINChatPin(String loggedINChatPin) {
        this.loggedINChatPin = loggedINChatPin;
    }

    public String getRole_val_det() {
        return role_val_det;
    }

    public void setRole_val_det(String role_val_det) {
        this.role_val_det = role_val_det;
    }

    @Override
    public String toString() {
        return "UserDetailDto{" +
                "loggedINEmail='" + loggedINEmail + '\'' +
                ", loggedINChatPin='" + loggedINChatPin + '\'' +
                ", role_val_det='" + role_val_det + '\'' +
                '}';
    }
}
