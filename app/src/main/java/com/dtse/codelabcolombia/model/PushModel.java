package com.dtse.codelabcolombia.model;

import com.google.gson.annotations.SerializedName;

public class PushModel {
    @SerializedName("validate_only")
    private boolean validate_only;
    @SerializedName("message")
    Message MessageObject;


    // Getter Methods

    public boolean getValidate_only() {
        return validate_only;
    }

    public Message getMessage() {
        return MessageObject;
    }

    // Setter Methods

    public void setValidate_only(boolean validate_only) {
        this.validate_only = validate_only;
    }

    public void setMessage(Message messageObject) {
        this.MessageObject = messageObject;
    }
}

