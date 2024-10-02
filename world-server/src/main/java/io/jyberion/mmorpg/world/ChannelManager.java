package io.jyberion.mmorpg.world;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    private static final ConcurrentHashMap<String, ChannelInfo> channels = new ConcurrentHashMap<>();

    public static void registerChannel(ChannelInfo channelInfo) {
        channels.put(channelInfo.getName(), channelInfo);
    }

    public static void updateHeartbeat(String channelName) {
        ChannelInfo channel = channels.get(channelName);
        if (channel != null) {
            channel.setLastHeartbeat(System.currentTimeMillis());
        }
    }

    public static Collection<ChannelInfo> getAvailableChannels() {
        return channels.values();
    }

    // Optional: Implement removal of stale channels
}
