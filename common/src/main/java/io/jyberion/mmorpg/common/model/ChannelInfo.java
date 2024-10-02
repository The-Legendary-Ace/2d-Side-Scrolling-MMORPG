package io.jyberion.mmorpg.common.model;

import java.io.Serializable;

public class ChannelInfo implements Serializable {
    private String name;
    private String address;
    private int port;
    private int currentPlayers;
    private int maxPlayers;
    private transient long lastHeartbeat;

    public ChannelInfo() {}

    public ChannelInfo(String name, String address, int port, int currentPlayers, int maxPlayers) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.lastHeartbeat = System.currentTimeMillis();
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
        return currentPlayers;
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
        this.currentPlayers = playerCount;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
        public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }
}
