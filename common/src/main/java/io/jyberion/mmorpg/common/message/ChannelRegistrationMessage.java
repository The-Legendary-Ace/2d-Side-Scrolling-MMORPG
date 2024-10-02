package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.io.Serializable;

public class ChannelRegistrationMessage implements Message, Serializable {
    private ChannelInfo channelInfo;

    public ChannelRegistrationMessage() {}

    public ChannelRegistrationMessage(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHANNEL_REGISTRATION;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }
}
