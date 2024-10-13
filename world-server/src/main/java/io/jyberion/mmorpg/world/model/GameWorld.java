package io.jyberion.mmorpg.world.model;

import io.jyberion.mmorpg.common.message.PlayerActionMessage;
import java.util.HashMap;
import java.util.Map;

public class GameWorld {

    private String worldId;
    private Map<String, String> players = new HashMap<>(); // Map to store players (e.g., playerId -> playerName)

    public GameWorld(String worldId) {
        this.worldId = worldId;
    }

    public void addPlayer(String playerId) {
        players.put(playerId, playerId);
        System.out.println("Player added to the game world: " + playerId);
    }

    public void processPlayerAction(String playerId, PlayerActionMessage actionMessage) {
        // Logic to process player action
        System.out.println("Processing action " + actionMessage.getActionType() + " for player: " + playerId);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
        System.out.println("Player removed from the game world: " + playerId);
    }

    public String getWorldId() {
        return worldId;
    }
}
