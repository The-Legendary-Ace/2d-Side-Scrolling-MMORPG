package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.io.Serializable;
import java.util.List;

public class LoginResponseMessage implements Message, Serializable {
    private boolean success;
    private String token;
    private String message;
    private List<ChannelInfo> channels;

    public LoginResponseMessage() {}

    // Constructor with all parameters
    public LoginResponseMessage(boolean success, String token, String message, List<ChannelInfo> channels) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.channels = channels;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOGIN_RESPONSE;
    }

    // Getter and Setter methods

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }
}
