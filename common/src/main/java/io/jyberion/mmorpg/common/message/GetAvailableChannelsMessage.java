package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class GetAvailableChannelsMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public MessageType getType() {
        return MessageType.GET_AVAILABLE_CHANNELS;
    }
}
