package com.dtse.codelabcolombia.model;

import com.google.gson.annotations.SerializedName;

public class ClickAction {
    @SerializedName("type")
    private float type;


    // Getter Methods

    public float getType() {
        return type;
    }

    // Setter Methods

    public void setType(float type) {
        this.type = type;
    }
}
