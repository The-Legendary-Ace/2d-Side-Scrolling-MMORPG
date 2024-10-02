package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class PlayerActionMessage implements Message, Serializable {
    private String actionType;
    private String data;

    public PlayerActionMessage() {}

    public PlayerActionMessage(String actionType, String data) {
        this.actionType = actionType;
        this.data = data;
    }

    @Override
    public MessageType getType() {
        return MessageType.PLAYER_ACTION;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
