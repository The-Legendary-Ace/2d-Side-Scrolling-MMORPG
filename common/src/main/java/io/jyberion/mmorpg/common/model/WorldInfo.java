package io.jyberion.mmorpg.common.model;

import java.util.List;

public class WorldInfo {
    private String worldName;
    private List<ChannelInfo> channels;

    public WorldInfo(String worldName, List<ChannelInfo> channels) {
        this.worldName = worldName;
        this.channels = channels;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void addChannel(ChannelInfo channel) {
        channels.add(channel);
    }
}
