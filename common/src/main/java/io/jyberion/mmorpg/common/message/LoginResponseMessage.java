package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.util.List;

public class LoginResponseMessage implements Message {
    private boolean success;
    private String token;
    private String message;
    private List<ChannelInfo> channels;

    public LoginResponseMessage(boolean success, String token, String message, List<ChannelInfo> channels) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.channels = channels;
    }

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

    @Override
    public MessageType getType() {
        return MessageType.LOGIN_RESPONSE;
    }
}