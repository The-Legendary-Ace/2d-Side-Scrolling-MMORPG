package io.jyberion.mmorpg.common.model;

public class ChannelInfo {
    private String worldName;
    private String channelName;
    private int currentPlayers;
    private int maxPlayers;
    private int status; // Status to represent whether the channel is online/offline
    private long lastHeartbeat; // New field for last heartbeat timestamp

    // Constructor for initializing basic channel information and players count
    public ChannelInfo(String worldName, String channelName, int currentPlayers, int maxPlayers, int status) {
        this.worldName = worldName;
        this.channelName = channelName;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.status = status;
        this.lastHeartbeat = System.currentTimeMillis(); // Initialize heartbeat with current time
    }

    // Getter for world name
    public String getWorldName() {
        return worldName;
    }

    // Setter for world name
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    // Getter for channel name
    public String getChannelName() {
        return channelName;
    }

    // Setter for channel name
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    // Getter for the current number of players in the channel
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    // Setter for the current number of players in the channel
    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    // Getter for the maximum number of players allowed in the channel
    public int getMaxPlayers() {
        return maxPlayers;
    }

    // Setter for the maximum number of players allowed in the channel
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    // Getter for the channel's status (e.g., online/offline)
    public int getStatus() {
        return status;
    }

    // Setter for the channel's status (e.g., online/offline)
    public void setStatus(int status) {
        this.status = status;
    }

    // Getter for the last heartbeat timestamp
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    // Setter for the last heartbeat timestamp
    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    // Method to update heartbeat to the current time
    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
}
