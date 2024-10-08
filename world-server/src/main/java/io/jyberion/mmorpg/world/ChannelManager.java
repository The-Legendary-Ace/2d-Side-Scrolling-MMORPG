package io.jyberion.mmorpg.world;

import io.jyberion.mmorpg.common.model.WorldInfo;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelManager {
    private Map<String, WorldInfo> worldMap = new HashMap<>(); // Map world names to WorldInfo

    public void addWorld(String worldName, List<ChannelInfo> channels) {
        worldMap.put(worldName, new WorldInfo(worldName, channels));
    }

    public WorldInfo getWorld(String worldName) {
        return worldMap.get(worldName);
    }

    public ChannelInfo getChannel(String worldName, String channelName) {
        WorldInfo worldInfo = worldMap.get(worldName);
        if (worldInfo != null) {
            for (ChannelInfo channel : worldInfo.getChannels()) {
                if (channel.getChannelName().equals(channelName)) {
                    return channel;
                }
            }
        }
        return null;
    }

    public void updateChannelHeartbeat(String worldName, String channelName, long heartbeat) {
        ChannelInfo channel = getChannel(worldName, channelName);
        if (channel != null) {
            channel.setLastHeartbeat(heartbeat);
        }
    }
}
