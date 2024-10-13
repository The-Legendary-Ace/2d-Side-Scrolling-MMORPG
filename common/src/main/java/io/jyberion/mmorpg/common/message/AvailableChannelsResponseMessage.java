package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.util.List;

public class AvailableChannelsResponseMessage implements Message {
    private static final long serialVersionUID = 1L;

    private List<ChannelInfo> channels;

    // No-argument constructor for deserialization
    public AvailableChannelsResponseMessage() {
    }

    // Constructor to initialize the message with available channels
    public AvailableChannelsResponseMessage(List<ChannelInfo> channels) {
        this.channels = channels;
    }

    // Getter for the channels list
    public List<ChannelInfo> getChannels() {
        return channels;
    }

    // Setter for the channels list (optional, if needed)
    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }

    @Override
    public MessageType getType() {
        return MessageType.AVAILABLE_CHANNELS_RESPONSE;
    }
}
