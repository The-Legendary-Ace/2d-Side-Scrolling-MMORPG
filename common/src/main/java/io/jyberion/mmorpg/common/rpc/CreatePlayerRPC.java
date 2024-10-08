package io.jyberion.mmorpg.common.rpc;

public interface CreatePlayerRPC {

    /**
     * Method to create a player on the channel server.
     * This will be called by the world server.
     *
     * @param username The username of the player.
     * @param worldName The world in which the player is being created.
     * @param channelName The channel in which the player is being created.
     * @return true if successful, false otherwise.
     */
    boolean createPlayer(String username, String worldName, String channelName);
}
