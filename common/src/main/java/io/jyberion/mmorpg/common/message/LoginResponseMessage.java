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
    private List<ChannelInfo> channels;
    private boolean banned;
    private String banReason;

    // No-argument constructor for Jackson
    public LoginResponseMessage() {
    }

    @JsonCreator
    public LoginResponseMessage(
            @JsonProperty("type") String type,  // Adding type to the constructor
            @JsonProperty("success") boolean success,
            @JsonProperty("token") String token,
            @JsonProperty("message") String message,
            @JsonProperty("channels") List<ChannelInfo> channels,
            @JsonProperty("banned") boolean banned,
            @JsonProperty("banReason") String banReason) {
        this.type = type;
        this.success = success;
        this.token = token;
        this.message = message;
        this.channels = channels;
        this.banned = banned;
        this.banReason = banReason;
    }

    // Getters and setters
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

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
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
}
