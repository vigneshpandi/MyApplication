package com.bluemapletech.hippatextapp.model;

import android.graphics.Bitmap;

/**
 * Created by Win7v5 on 10/24/2016.
 */

public class Message {


    private String mtext;
    private String msender;
    private String toChatEmail;
    private String senderId;
    private String randomValue;
    private String pushNotificationId;
    private String image;
    public String getMtext() {
        return mtext;
    }

    public void setMtext(String mtext) {
        this.mtext = mtext;
    }

    public String getMsender() {
        return msender;
    }

    public void setMsender(String msender) {
        this.msender = msender;
    }

    public String getToChatEmail() {
        return toChatEmail;
    }

    public void setToChatEmail(String toChatEmail) {
        this.toChatEmail = toChatEmail;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRandomValue() {
        return randomValue;
    }

    public void setRandomValue(String randomValue) {
        this.randomValue = randomValue;
    }

    public String getPushNotificationId() {
        return pushNotificationId;
    }

    public void setPushNotificationId(String pushNotificationId) {
        this.pushNotificationId = pushNotificationId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Message{" +
                "mtext='" + mtext + '\'' +
                ", msender='" + msender + '\'' +
                ", toChatEmail='" + toChatEmail + '\'' +
                ", senderId='" + senderId + '\'' +
                ", randomValue='" + randomValue + '\'' +
                ", pushNotificationId='" + pushNotificationId + '\'' +
                ", image='" + image + '\'' +
                '}';
    }


}
