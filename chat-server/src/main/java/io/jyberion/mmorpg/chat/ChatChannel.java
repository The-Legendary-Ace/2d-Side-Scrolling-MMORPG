package io.jyberion.mmorpg.chat;

import io.netty.channel.Channel;
import java.util.concurrent.ConcurrentHashMap;

public class ChatChannel {
    private final String channelName;
    private final ConcurrentHashMap<String, Channel> participants = new ConcurrentHashMap<>();

    public ChatChannel(String channelName) {
        this.channelName = channelName;
    }

    public void addParticipant(String username, Channel channel) {
        participants.put(username, channel);
    }

    public void removeParticipant(String username) {
        participants.remove(username);
    }

    public void broadcastMessage(ChatMessage msg) {
        participants.values().forEach(channel -> {
            channel.writeAndFlush(msg);
        });
    }

    public void sendMessage(ChatMessage msg) {
        Channel recipientChannel = participants.get(msg.getRecipient());
        if (recipientChannel != null) {
            recipientChannel.writeAndFlush(msg);
        }
    }

    public int getParticipantCount() {
        return participants.size();
    }
}
