package io.jyberion.mmorpg.chat;

import io.jyberion.mmorpg.common.message.ChatMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatChannelManager {

    private static final ChatChannelManager instance = new ChatChannelManager();

    private final ChannelGroup globalChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // You can have additional channel groups for guilds, local areas, etc.

    private ChatChannelManager() {}

    public static ChatChannelManager getInstance() {
        return instance;
    }

    public void addChannel(Channel channel) {
        globalChannels.add(channel);
    }

    public void removeChannel(Channel channel) {
        globalChannels.remove(channel);
    }

    public void broadcastToGlobal(ChatMessage msg) {
        globalChannels.writeAndFlush(msg);
    }

    // Implement methods for guild, private, and local messages
    public void broadcastToGuild(String sender, ChatMessage msg) {
        // Implementation for broadcasting to a guild
    }

    public void sendPrivateMessage(String sender, String recipient, ChatMessage msg) {
        // Implementation for sending a private message
    }

    public void broadcastToLocal(String sender, ChatMessage msg) {
        // Implementation for broadcasting to a local area
    }
}
