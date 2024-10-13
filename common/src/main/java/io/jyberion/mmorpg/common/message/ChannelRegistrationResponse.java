package io.jyberion.mmorpg.common.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public class ChannelRegistrationResponse implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String errorMessage;

    // Default constructor
    public ChannelRegistrationResponse() {
    }

    // Constructor
    public ChannelRegistrationResponse(boolean success) {
        this.success = success;
        this.errorMessage = success ? null : "Unknown error occurred during registration.";
    }

    public ChannelRegistrationResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MessageType getType() {
        return MessageType.CHANNEL_REGISTRATION_RESPONSE;
    }
}
