package io.jyberion.mmorpg.common.model;

import javax.persistence.*;
import java.time.Instant;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelStatus status; // Enum for status (ONLINE, OFFLINE, MAINTENANCE)

    @Column(nullable = false)
    private long lastHeartbeat; // Last heartbeat timestamp

    @Column(nullable = false)
    private long createdAt; // Timestamp for when the channel was created

    @Column(nullable = false)
    private long updatedAt; // Timestamp for last update

    public ChannelInfo() {
        // Default constructor needed by JPA
    }

    public ChannelInfo(String worldName, String channelName, String host, int port, int currentPlayers, int maxPlayers, ChannelStatus status) {
        this.worldName = worldName;
        this.channelName = channelName;
        this.host = host;
        this.port = port;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.status = status;
        this.lastHeartbeat = System.currentTimeMillis(); // Initialize heartbeat
        this.createdAt = System.currentTimeMillis(); // Set creation time
        this.updatedAt = System.currentTimeMillis(); // Set initial update time
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWorldName() { return worldName; }
    public void setWorldName(String worldName) { this.worldName = worldName; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int getCurrentPlayers() { return currentPlayers; }
    public void setCurrentPlayers(int currentPlayers) { this.currentPlayers = currentPlayers; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public ChannelStatus getStatus() { return status; }
    public void setStatus(ChannelStatus status) { this.status = status; }

    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = System.currentTimeMillis(); // Update the timestamp when the entity is updated
    }
}
