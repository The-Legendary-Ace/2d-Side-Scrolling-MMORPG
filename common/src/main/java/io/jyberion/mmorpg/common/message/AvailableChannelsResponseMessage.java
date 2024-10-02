package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.io.Serializable;
import java.util.List;

public class AvailableChannelsResponseMessage implements Message, Serializable {
    private List<ChannelInfo> channels;

    public AvailableChannelsResponseMessage() {}

    public AvailableChannelsResponseMessage(List<ChannelInfo> channels) {
        this.channels = channels;
    }

    @Override
    public MessageType getType() {
        return MessageType.AVAILABLE_CHANNELS_RESPONSE;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }
}
