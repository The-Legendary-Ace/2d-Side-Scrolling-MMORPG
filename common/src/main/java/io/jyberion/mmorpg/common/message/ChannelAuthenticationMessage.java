package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class ChannelAuthenticationMessage implements Message, Serializable {
    private String token;

    public ChannelAuthenticationMessage() {}

    public ChannelAuthenticationMessage(String token) {
        this.token = token;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_AUTHENTICATION;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
