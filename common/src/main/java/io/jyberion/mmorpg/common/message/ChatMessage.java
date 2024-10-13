package io.jyberion.mmorpg.common.message; 

import java.io.Serializable;

public class ChatMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String channelType;
    private String content;
    private String token;
    private String recipient; // For private messages

    // No-argument constructor
    public ChatMessage() {
    }

    // Constructor with token (e.g., when receiving from clients)
    public ChatMessage(String token, String channelType, String content) {
        this.token = token;
        this.channelType = channelType;
        this.content = content;
    }

    // Constructor with recipient (for private messages)
    public ChatMessage(String token, String channelType, String content, String recipient) {
        this.token = token;
        this.channelType = channelType;
        this.content = content;
        this.recipient = recipient;
    }

    // Full constructor
    public ChatMessage(String sender, String channelType, String content, String token, String recipient) {
        this.sender = sender;
        this.channelType = channelType;
        this.content = content;
        this.token = token;
        this.recipient = recipient;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public MessageType getType() {
        return MessageType.CHAT;
    }
}
