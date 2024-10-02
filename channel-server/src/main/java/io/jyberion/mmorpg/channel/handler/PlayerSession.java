package io.jyberion.mmorpg.channel.handler;

import io.netty.channel.Channel;

public class PlayerSession {
    private final String username;
    private final Channel channel;

    public PlayerSession(String username, Channel channel) {
        this.username = username;
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public Channel getChannel() {
        return channel;
    }
}
