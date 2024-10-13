package io.jyberion.mmorpg.common.model;

import javax.persistence.*;

@Entity
@Table(name = "channels")
public class ChannelInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String worldName;

    @Column(nullable = false)
    private String channelName;

    @Column(nullable = false)
    private String host; // Host address for the channel

    @Column(nullable = false)
    private int port; // Port for the channel

    @Column(nullable = false)
    private int currentPlayers;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int status; // Status to represent whether the channel is online/offline

    @Column(nullable = false)
    private long lastHeartbeat; // New field for last heartbeat timestamp

    // Constructor for initializing basic channel information and players count
    public ChannelInfo() {
        // Default constructor needed by JPA
    }

    public ChannelInfo(String worldName, String channelName, String host, int port, int currentPlayers, int maxPlayers, int status) {
        this.worldName = worldName;
        this.channelName = channelName;
        this.host = host;
        this.port = port;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.status = status;
        this.lastHeartbeat = System.currentTimeMillis(); // Initialize heartbeat with current time
    }

    // Getters and Setters for each field

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
}
