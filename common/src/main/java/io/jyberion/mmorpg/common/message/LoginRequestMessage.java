package io.jyberion.mmorpg.common.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)  // Ignore unrecognized properties like "type"
public class LoginRequestMessage implements Message {
    private String username;
    private String password;
    private static final long serialVersionUID = 1L;

    // Default constructor for Jackson
    @JsonCreator
    public LoginRequestMessage(@JsonProperty("username") String username, @JsonProperty("password") String password) {
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
}
