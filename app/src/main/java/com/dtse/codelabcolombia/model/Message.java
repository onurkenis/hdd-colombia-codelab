package com.dtse.codelabcolombia.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Message {
    @SerializedName("notification")
    Notification notification;
    @SerializedName("android")
    Android android;
    @SerializedName("token")
    ArrayList < Object > token = new ArrayList< Object >();


    // Getter Methods

    public Notification getNotification() {
        return notification;
    }

    public Android getAndroid() {
        return android;
    }

    // Setter Methods

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void setAndroid(Android android) {
        this.android = android;
    }

    public void addToken(String token)
    {
        this.token.add(token);
    }
}
