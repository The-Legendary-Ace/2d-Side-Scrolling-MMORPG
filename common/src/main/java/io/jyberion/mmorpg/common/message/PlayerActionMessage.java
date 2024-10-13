package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class PlayerActionMessage implements Message, Serializable {
    private static final long serialVersionUID = 1L;

    private String actionType;
    private String playerId;
    private String data;

    public PlayerActionMessage() {}

    public PlayerActionMessage(String actionType, String playerId, String data) {
        this.actionType = actionType;
        this.playerId = playerId;
        this.data = data;
    }

    public MessageType getType() {
        return MessageType.PLAYER_ACTION;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PlayerActionMessage{" +
                "actionType='" + actionType + '\'' +
                ", playerId='" + playerId + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
