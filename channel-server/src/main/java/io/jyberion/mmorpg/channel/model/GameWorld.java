package io.jyberion.mmorpg.channel.model;

import io.jyberion.mmorpg.common.message.PlayerActionMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameWorld {
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();

    public void addPlayer(String username) {
        players.put(username, new PlayerState(username));
    }

    public void removePlayer(String username) {
        players.remove(username);
    }

    public void processPlayerAction(String username, PlayerActionMessage msg) {
        PlayerState player = players.get(username);
        if (player != null) {
            // Process the action based on actionType
            String actionType = msg.getActionType();
            String data = msg.getData();

            switch (actionType) {
                case "MOVE":
                    // Update player position
                    player.move(data);
                    break;
                case "ATTACK":
                    // Handle attack logic
                    player.attack(data);
                    break;
                // Add more cases for different actions
                default:
                    // Unknown action
                    break;
            }
        }
    }
}
