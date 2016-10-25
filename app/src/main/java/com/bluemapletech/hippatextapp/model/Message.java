package com.bluemapletech.hippatextapp.model;

/**
 * Created by Win7v5 on 10/24/2016.
 */

public class Message {


    private String mtext;
    private String msender;
    private String toChatEmail;

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

    @Override
    public String toString() {
        return "Message{" +
                "mtext='" + mtext + '\'' +
                ", msender='" + msender + '\'' +
                ", toChatEmail='" + toChatEmail + '\'' +
                '}';
    }
}
