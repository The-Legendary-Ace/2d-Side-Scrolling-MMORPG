package io.jyberion.mmorpg.common.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.util.List;

@JsonTypeName("LOGIN_RESPONSE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseMessage implements Message {

    private String type = "LOGIN_RESPONSE"; // Explicit type field
    private boolean success;
    private String token;
    private String message;
    private List<WorldWithChannels> worlds; // Add this field to store the world data
    private boolean banned;
    private String banReason;

    // No-argument constructor for Jackson
    public LoginResponseMessage() {
    }

    @JsonCreator
    public LoginResponseMessage(
            @JsonProperty("type") String type,
            @JsonProperty("success") boolean success,
            @JsonProperty("token") String token,
            @JsonProperty("message") String message,
            @JsonProperty("worlds") List<WorldWithChannels> worlds, // Adjusted field to include worlds
            @JsonProperty("banned") boolean banned,
            @JsonProperty("banReason") String banReason) {
        this.type = type;
        this.success = success;
        this.token = token;
        this.message = message;
        this.worlds = worlds;
        this.banned = banned;
        this.banReason = banReason;
    }

    // Add a getter method for worlds
    public List<WorldWithChannels> getWorlds() {
        return worlds;
    }

    // Other getters and setters remain the same
    @Override
    public MessageType getType() {
        return MessageType.LOGIN_RESPONSE;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    // Nested class to represent a world and its channels
    public static class WorldWithChannels {
        private String worldName;
        private List<ChannelInfo> channels;

        public WorldWithChannels() {
            // No-argument constructor for Jackson
        }

        public WorldWithChannels(String worldName, List<ChannelInfo> channels) {
            this.worldName = worldName;
            this.channels = channels;
        }

        public String getWorldName() {
            return worldName;
        }

        public void setWorldName(String worldName) {
            this.worldName = worldName;
        }

        public List<ChannelInfo> getChannels() {
            return channels;
        }

        public void setChannels(List<ChannelInfo> channels) {
            this.channels = channels;
        }
    }
}
