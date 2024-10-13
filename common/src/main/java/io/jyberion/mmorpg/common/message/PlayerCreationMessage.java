package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class PlayerCreationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String channelName;

    public PlayerCreationMessage(String username, String channelName) {
        this.username = username;
        this.channelName = channelName;
    }

    public String getUsername() {
        return username;
    }

    public String getChannelName() {
        return channelName;
    }
}
