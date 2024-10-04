package io.jyberion.mmorpg.common.message;

public class LoginRequestMessage implements Message {
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    // Getters and setters
}
