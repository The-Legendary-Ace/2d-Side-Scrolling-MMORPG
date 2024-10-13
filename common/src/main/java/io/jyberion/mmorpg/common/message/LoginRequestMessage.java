package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class LoginRequestMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    // Constructors
    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOGIN_REQUEST;
    }
}
