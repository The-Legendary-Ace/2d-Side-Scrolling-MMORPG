package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class ChannelHeartbeatMessage implements Message, Serializable {
    private String channelName;

    public ChannelHeartbeatMessage() {}

    public ChannelHeartbeatMessage(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_HEARTBEAT;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
