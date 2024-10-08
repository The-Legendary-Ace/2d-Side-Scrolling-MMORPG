package io.jyberion.mmorpg.common.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequestMessage.class, name = "LOGIN_REQUEST"),
        @JsonSubTypes.Type(value = LoginResponseMessage.class, name = "LOGIN_RESPONSE")
})
public interface Message {
    MessageType getType();
}