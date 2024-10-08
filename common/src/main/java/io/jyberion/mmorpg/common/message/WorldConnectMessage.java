package io.jyberion.mmorpg.common.message;

public class WorldConnectMessage implements Message {
    private String sessionId;

    public WorldConnectMessage(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public MessageType getType() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
