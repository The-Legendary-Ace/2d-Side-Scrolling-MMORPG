package io.jyberion.mmorpg.common.message;

import io.jyberion.mmorpg.common.model.ChannelInfo;

import java.io.Serializable;
import java.util.List;

public class ChannelAvailabilityResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ChannelInfo> availableChannels;

    public ChannelAvailabilityResponse(List<ChannelInfo> availableChannels) {
        this.availableChannels = availableChannels;
    }

    public List<ChannelInfo> getAvailableChannels() {
        return availableChannels;
    }
}
