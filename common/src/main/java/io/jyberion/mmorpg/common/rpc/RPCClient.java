package io.jyberion.mmorpg.common.rpc;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.model.ChannelInfo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class RPCClient {

    private static final String WORLD_SERVER_NAME = ConfigLoader.getProperty("world.server.name");
    private static final String WORLD_SERVER_ADDRESS = ConfigLoader.getProperty("world.server.address");
    private static final int WORLD_SERVER_PORT = Integer.parseInt(ConfigLoader.getProperty("world.server.port"));

    // Method to call the World Server and get the available channels
    public List<ChannelInfo> getChannelList() {
        try (Socket socket = new Socket(WORLD_SERVER_ADDRESS, WORLD_SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Send a request to get channel list
            out.writeObject("GetChannelList");
            out.writeObject(WORLD_SERVER_NAME);

            // Receive response
            return (List<ChannelInfo>) in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Existing player creation method
    public boolean callCreatePlayer(String username, String channelName) {
        try (Socket socket = new Socket(WORLD_SERVER_ADDRESS, WORLD_SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Send a request to create a player
            out.writeObject("CreatePlayer");
            out.writeObject(username);
            out.writeObject(WORLD_SERVER_NAME);
            out.writeObject(channelName);

            // Receive response
            return in.readBoolean();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
