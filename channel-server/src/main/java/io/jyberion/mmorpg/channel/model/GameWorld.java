package io.jyberion.mmorpg.channel.model;

import io.jyberion.mmorpg.common.entity.Player;
import io.jyberion.mmorpg.common.message.PlayerActionMessage;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.jyberion.mmorpg.common.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GameWorld {
    private static final Logger logger = LoggerFactory.getLogger(GameWorld.class);
    private final Map<String, PlayerState> players = new ConcurrentHashMap<>();

    public CompletableFuture<Void> addPlayer(String token) {
        String username = TokenUtil.validateToken(token);
        if (username == null) {
            logger.warn("Invalid token");
            return CompletableFuture.completedFuture(null);
        }

        return PlayerService.getPlayerAsync(username).thenAccept(player -> {
            if (player != null) {
                logger.info("Player {} loaded from the database.", player.getUsername());
                PlayerState playerState = new PlayerState(username);
                playerState.setLevel(player.getLevel());
                playerState.setExperience(player.getExperience());
                players.put(username, playerState);
            } else {
                logger.info("Player {} not found in the database, creating new player.", username);
                Player newPlayer = new Player(username);
                PlayerService.savePlayerAsync(newPlayer).thenRun(() -> {
                    logger.info("New player {} has been created and saved to the database.", username);
                });

                players.put(username, new PlayerState(username));
            }
        });
    }

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
                    player.setLevel(playerState.getLevel());
                    player.setExperience(playerState.getExperience());

                    PlayerService.savePlayerAsync(player).thenRun(() -> {
                        logger.info("Player {} has been saved to the database and removed from the game.", username);
                    });
                }
            });
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    public void processPlayerAction(String token, PlayerActionMessage msg) {
        String username = TokenUtil.validateToken(token);
        if (username == null) {
            logger.warn("Invalid token");
            return;
        }

        PlayerState player = players.get(username);
        if (player != null) {
            String actionType = msg.getActionType();
            String data = msg.getData();

            switch (actionType) {
                case "MOVE":
                    player.move(data);
                    break;
                case "ATTACK":
                    player.attack(data);
                    break;
                default:
                    logger.warn("Unknown action type: {}", actionType);
                    break;
            }

            PlayerService.getPlayerAsync(username).thenAccept(persistentPlayer -> {
                if (persistentPlayer != null) {
                    persistentPlayer.setLevel(player.getLevel());
                    persistentPlayer.setExperience(player.getExperience());

                    PlayerService.savePlayerAsync(persistentPlayer).thenRun(() -> {
                        logger.info("Player {}'s state has been updated in the database.", username);
                    });
                }
            });
        }
    }
}
