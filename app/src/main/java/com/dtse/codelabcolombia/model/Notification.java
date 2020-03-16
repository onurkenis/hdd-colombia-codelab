package com.dtse.codelabcolombia.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("click_action")
    ClickAction click_action;


    // Getter Methods

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public ClickAction getClick_action() {
        return click_action;
    }

    // Setter Methods

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setClick_action(ClickAction click_action) {
        this.click_action = click_action;
    }
}
