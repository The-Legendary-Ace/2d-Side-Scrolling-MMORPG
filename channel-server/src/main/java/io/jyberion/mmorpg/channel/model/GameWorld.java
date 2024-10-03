package io.jyberion.mmorpg.channel.model;

import io.jyberion.mmorpg.common.entity.Player;
import io.jyberion.mmorpg.common.message.PlayerActionMessage;
import io.jyberion.mmorpg.common.service.PlayerService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GameWorld {

    // In-memory representation of players currently in the game world
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();

    // Add player to the game world and load their data from the database asynchronously
    public CompletableFuture<Void> addPlayer(String username) {
        return PlayerService.getPlayerAsync(username).thenAccept(player -> {
            if (player != null) {
                System.out.println("Player " + player.getUsername() + " loaded from the database.");
                // Initialize the player's in-game state
                players.put(username, new PlayerState(username));
            } else {
                System.out.println("Player not found in the database, initializing new player.");
                // Create a new player in the database if they don’t exist
                Player newPlayer = new Player();
                newPlayer.setUsername(username);
                newPlayer.setLevel(1); // Default starting level
                newPlayer.setExperience(0); // Default starting experience

                PlayerService.savePlayerAsync(newPlayer).thenRun(() -> {
                    System.out.println("New player " + username + " has been created and saved to the database.");
                });

                // Initialize the player's in-game state
                players.put(username, new PlayerState(username));
            }
        });
    }

    // Remove player from the game world and save their state to the database
    public CompletableFuture<Void> removePlayer(String username) {
        PlayerState playerState = players.remove(username);
        if (playerState != null) {
            return PlayerService.getPlayerAsync(username).thenAccept(player -> {
                if (player != null) {
                    // Save the player's current state (e.g., position, stats)
                    player.setLevel(playerState.getLevel());
                    player.setExperience(playerState.getExperience());

                    PlayerService.savePlayerAsync(player).thenRun(() -> {
                        System.out.println("Player " + username + " has been saved to the database and removed from the game.");
                    });
                }
            });
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    // Process player actions and update both in-memory and persistent states
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

            // Persist player state updates asynchronously
            PlayerService.getPlayerAsync(username).thenAccept(persistentPlayer -> {
                if (persistentPlayer != null) {
                    // Update the persistent player state with in-game changes
                    persistentPlayer.setLevel(player.getLevel());
                    persistentPlayer.setExperience(player.getExperience());

                    PlayerService.savePlayerAsync(persistentPlayer).thenRun(() -> {
                        System.out.println("Player " + username + "'s state has been updated in the database.");
                    });
                }
            });
        }
    }
}
