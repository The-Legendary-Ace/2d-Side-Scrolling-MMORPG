package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class ChannelRegistrationMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private String worldId;
    private String channelName;
    private String host;
    private int port;
    private int currentPlayers;
    private int maxPlayers;
    private String status;

    // No-argument constructor for deserialization
    public ChannelRegistrationMessage() {
        System.out.println("ChannelRegistrationMessage: Default constructor called for deserialization.");
    }

    // Constructor with additional fields
    public ChannelRegistrationMessage(String worldId, String channelName, String host, int port, int currentPlayers, int maxPlayers, String status) {
        this.worldId = worldId;
        this.channelName = channelName;
        this.host = host;
        this.port = port;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.status = status;
        System.out.println("ChannelRegistrationMessage: Initialized with worldId=" + worldId +
                ", channelName=" + channelName + ", host=" + host + ", port=" + port +
                ", currentPlayers=" + currentPlayers + ", maxPlayers=" + maxPlayers + ", status=" + status);
    }

    // Getters for all fields
    public String getWorldId() {
        return worldId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_REGISTRATION;
    }
}
