package com.dtse.codelabcolombia.model;

import com.google.gson.annotations.SerializedName;

public class Android {
    @SerializedName("notification")
    Notification notification;


    // Getter Methods

    public Notification getNotification() {
        return notification;
    }

    // Setter Methods

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
