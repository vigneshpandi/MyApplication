package com.bluemapletech.hippatextapp.model;

import java.io.Serializable;

/**
 * Created by Win7v5 on 11/7/2016.
 */

public class Groups implements Serializable {
    private String admin;
    private String groupEmailId;
    private String groupImage;
    private String groupName;
    private String randomName;
    private String status;
    private String userMail;
    private String userImage;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGroupEmailId() {
        return groupEmailId;
    }

    public void setGroupEmailId(String groupEmailId) {
        this.groupEmailId = groupEmailId;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRandomName() {
        return randomName;
    }

    public void setRandomName(String randomName) {
        this.randomName = randomName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "admin='" + admin + '\'' +
                ", groupEmailId='" + groupEmailId + '\'' +
                ", groupImage='" + groupImage + '\'' +
                ", groupName='" + groupName + '\'' +
                ", randomName='" + randomName + '\'' +
                ", status='" + status + '\'' +
                ", userMail='" + userMail + '\'' +
                ", userImage='" + userImage + '\'' +
                '}';
    }
}
