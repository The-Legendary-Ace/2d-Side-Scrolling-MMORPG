package io.jyberion.mmorpg.chat;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String sender;
    private String content;
    private String channelType; // GLOBAL, GUILD, PRIVATE
    private String recipient; // For private messages
    private String guildId;   // For guild messages
    private boolean isAnnouncement = false; // Flag for system announcements

    public ChatMessage() {}

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    // Getters and setters

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public boolean isAnnouncement() {
        return isAnnouncement;
    }

    public void setAnnouncement(boolean isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }
}
