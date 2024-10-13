package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class ChannelRegistrationMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private String worldId;
    private String channelName;
    private String host;
    private int port;

    // Constructors
    public ChannelRegistrationMessage() {
    }

    public ChannelRegistrationMessage(String worldId, String channelName, String host, int port) {
        this.worldId = worldId;
        this.channelName = channelName;
        this.host = host;
        this.port = port;
    }

    // Getters and Setters
    public String getWorldId() {
        return worldId;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_REGISTRATION;
    }
}
