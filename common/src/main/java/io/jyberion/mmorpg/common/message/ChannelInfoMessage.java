package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;

import java.io.Serializable;

public class ChannelInfoMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private String worldName;
    private String channelName;
    private int currentPlayers;
    private int maxPlayers;
    private int status;

    // Constructors
    public ChannelInfoMessage() {
    }

    public ChannelInfoMessage(String worldName, String channelName, int currentPlayers, int maxPlayers, int status) {
        this.worldName = worldName;
        this.channelName = channelName;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.status = status;
    }

    // Getters and Setters
    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_INFO;
    }
}
