package io.jyberion.mmorpg.common.message;

public enum MessageType {
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    CHANNEL_REGISTRATION,
    CHANNEL_REGISTRATION_RESPONSE,
    CHANNEL_DETAILS_REQUEST,
    CHANNEL_INFO,
    GET_AVAILABLE_CHANNELS,
    AVAILABLE_CHANNELS_RESPONSE,
    CHANNEL_HEARTBEAT,
    CHANNEL_AUTHENTICATION,
    CHANNEL_AUTHENTICATION_RESPONSE,
    CHAT,
    PLAYER_ACTION;
}
