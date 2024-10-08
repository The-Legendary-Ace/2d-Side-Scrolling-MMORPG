package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.io.Serializable;
import java.util.List;

public class LoginResponseMessage implements Message, Serializable {
    private boolean success;
    private String token;
    private String message;
    private List<ChannelInfo> channels;
    private boolean banned;
    private String banReason;

    // Add a new constructor for session ID
    public LoginResponseMessage(boolean success, String token, String message, List<ChannelInfo> channels, boolean banned, String banReason) {
        this.success = success;
        this.token = token;  // This is now the session ID
        this.message = message;
        this.channels = channels;
        this.banned = banned;
        this.banReason = banReason;
    }

    // Getters
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

    public boolean isBanned() {
        return banned;
    }

    public String getBanReason() {
        return banReason;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOGIN_RESPONSE;
    }
}
