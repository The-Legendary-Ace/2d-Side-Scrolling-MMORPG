package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class ChannelAuthenticationResponse implements Message, Serializable {
    private boolean success;
    private String message;

    public ChannelAuthenticationResponse() {}

    public ChannelAuthenticationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_REGISTRATION_RESPONSE;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
