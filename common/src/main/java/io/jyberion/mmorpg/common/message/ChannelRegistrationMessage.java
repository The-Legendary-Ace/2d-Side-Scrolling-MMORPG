package io.jyberion.mmorpg.common.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public class ChannelRegistrationMessage implements Message, Serializable {
    private String channelName;
    private String host;
    private int port;

    // No-argument constructor for serialization
    public ChannelRegistrationMessage() {}

    public ChannelRegistrationMessage(String channelName, String host, int port) {
        this.channelName = channelName;
        this.host = host;
        this.port = port;
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
    
    @JsonIgnore
    public MessageType getType() {
        return MessageType.CHANNEL_REGISTRATION;
    }
}
