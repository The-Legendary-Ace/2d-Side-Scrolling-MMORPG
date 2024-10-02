package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class LoginRequestMessage implements Message, Serializable {
    private String username;
    private String password;

    public LoginRequestMessage() {}

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOGIN_REQUEST;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Optional: Setter methods if needed
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
