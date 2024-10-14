package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.util.List;

public class AvailableChannelsResponseMessage implements Message {

    private List<ChannelInfo> channels;
    private MessageType messageType;

    // No-argument constructor required for Jackson deserialization
    public AvailableChannelsResponseMessage() {
        this.messageType = MessageType.AVAILABLE_CHANNELS_RESPONSE;
    }

    // Constructor to initialize with list of channels
    public AvailableChannelsResponseMessage(List<ChannelInfo> channels) {
        this.channels = channels;
        this.messageType = MessageType.AVAILABLE_CHANNELS_RESPONSE;
    }

    // Implement the required method from the Message interface
    @Override
    public MessageType getType() {
        return messageType;
    }

    // Getter for channels
    public List<ChannelInfo> getChannels() {
        return channels;
    }

    // Setter for channels
    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }
}
