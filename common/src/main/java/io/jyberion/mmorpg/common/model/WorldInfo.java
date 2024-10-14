package io.jyberion.mmorpg.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Entity
@Table(name = "world_servers")
public class WorldInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "World name cannot be empty")
    @Column(nullable = false, unique = true)
    private String worldName;

    @NotEmpty(message = "Host cannot be empty")
    @Column(nullable = false)
    private String host;

    @Positive(message = "Port must be a positive integer")
    @Column(nullable = false)
    private int port;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "world_id")
    private List<ChannelInfo> channels;

    // No-argument constructor for JPA
    public WorldInfo() {
    }

    // Constructor for initializing WorldInfo with worldName, host, port, and channels
    public WorldInfo(@NotEmpty String worldName, @NotEmpty String host, @Positive int port, List<ChannelInfo> channels) {
        this.worldName = worldName;
        this.host = host;
        this.port = port;
        this.channels = channels;
    }

    // Getters and Setters
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

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "WorldInfo{" +
                "id=" + id +
                ", worldName='" + worldName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", channels=" + channels +
                '}';
    }
}
