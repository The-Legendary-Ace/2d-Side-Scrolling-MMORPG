package io.jyberion.mmorpg.common.model;

import java.io.Serializable;

public class ChannelInfo implements Serializable {
    private String name;
    private String address;
    private int port;
    private int playerCount;
    private int maxPlayers;

    public ChannelInfo() {}

    public ChannelInfo(String name, String address, int port, int playerCount, int maxPlayers) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;
    }

    // Getter and Setter methods

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
