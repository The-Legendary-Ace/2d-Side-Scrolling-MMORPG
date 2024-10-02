package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class GetAvailableChannelsMessage implements Message, Serializable {
    public GetAvailableChannelsMessage() {}

    @Override
    public MessageType getType() {
        return MessageType.GET_AVAILABLE_CHANNELS;
    }
}
