package io.jyberion.mmorpg.world.model;

import io.jyberion.mmorpg.common.entity.Player;
import io.jyberion.mmorpg.common.message.PlayerActionMessage;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.jyberion.mmorpg.common.service.PlayerService;
import io.jyberion.mmorpg.common.entity.PlayerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GameWorld {
    private static final Logger logger = LoggerFactory.getLogger(GameWorld.class);

    // In-memory map to keep track of players currently in the game world
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();

    // Add player to the game world and load their data from the database asynchronously
    public CompletableFuture<Void> addPlayer(String token) {
        String username = TokenUtil.validateToken(token);
        if (username == null) {
            logger.warn("Invalid token");
            return CompletableFuture.completedFuture(null);
        }

        return PlayerService.getPlayerAsync(username).thenAccept(player -> {
            if (player != null) {
                logger.info("Player {} loaded from the database.", player.getUsername());
                // Initialize the player's in-game state with data from the database
                PlayerState playerState = new PlayerState(player.getUsername());
                playerState.setLevel(player.getLevel());
                playerState.setExperience(player.getExperience());
                players.put(username, playerState);
            } else {
                logger.info("Player {} not found in the database, creating new player.", username);
                // Create a new player in the database
                Player newPlayer = new Player();
                newPlayer.setUsername(username);
                newPlayer.setLevel(1);
                newPlayer.setExperience(0);

                PlayerService.savePlayerAsync(newPlayer).thenRun(() -> {
                    logger.info("New player {} has been created and saved to the database.", username);
                });

                // Initialize the player's in-game state
                players.put(username, new PlayerState(newPlayer.getUsername()));
            }
        }).exceptionally(ex -> {
            logger.error("Error adding player {} to the game world.", username, ex);
            return null;
        });
    }

    // Remove player from the game world and save their state to the database
    public CompletableFuture<Void> removePlayer(String token) {
        String username = TokenUtil.validateToken(token);
        if (username == null) {
            logger.warn("Invalid token");
            return CompletableFuture.completedFuture(null);
        }

        PlayerState playerState = players.remove(username);
        if (playerState != null) {
            return PlayerService.getPlayerAsync(username).thenAccept(player -> {
                if (player != null) {
                    // Save the player's current state (e.g., level, experience)
                    player.setLevel(playerState.getLevel());
                    player.setExperience(playerState.getExperience());

                    PlayerService.savePlayerAsync(player).thenRun(() -> {
                        logger.info("Player {} has been saved to the database and removed from the game.", username);
                    });
                }
            }).exceptionally(ex -> {
                logger.error("Error removing player {} from the game world.", username, ex);
                return null;
            });
        } else {
            logger.warn("Player {} not found in the game world.", username);
            return CompletableFuture.completedFuture(null);
        }
    }

    // Process player actions and update both in-memory and persistent states
    public void processPlayerAction(String token, PlayerActionMessage msg) {
        String username = TokenUtil.validateToken(token);
        if (username == null) {
            logger.warn("Invalid token");
            return;
        }

        PlayerState playerState = players.get(username);
        if (playerState != null) {
            // Process the action based on actionType
            String actionType = msg.getActionType();
            String data = msg.getData();

            switch (actionType) {
                case "MOVE":
                    // Update player position
                    playerState.move(data);
                    break;
                case "ATTACK":
                    // Handle attack logic
                    playerState.attack(data);
                    break;
                // Add more cases for different actions
                default:
                    logger.warn("Unknown action type: {}", actionType);
                    break;
            }

            // Persist player state updates asynchronously
            PlayerService.getPlayerAsync(username).thenAccept(player -> {
                if (player != null) {
                    // Update the persistent player state with in-game changes
                    player.setLevel(playerState.getLevel());
                    player.setExperience(playerState.getExperience());

                    PlayerService.savePlayerAsync(player).thenRun(() -> {
                        logger.info("Player {}'s state has been updated in the database.", username);
                    });
                }
            }).exceptionally(ex -> {
                logger.error("Error updating player {} in the database.", username, ex);
                return null;
            });
        } else {
            logger.warn("Player {} not found in the game world.", username);
        }
    }
}