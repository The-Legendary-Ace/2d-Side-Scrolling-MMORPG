package io.jyberion.mmorpg.channel;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChannelRPCServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9090)) {  // Channel server listens on port 9090
            System.out.println("Channel RPC Server started, waiting for connections...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    String method = (String) in.readObject();

                    if ("createPlayer".equals(method)) {
                        String username = (String) in.readObject();
                        String worldName = (String) in.readObject();
                        String channelName = (String) in.readObject();

                        boolean success = createPlayer(username, worldName, channelName);
                        out.writeObject(success);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createPlayer(String username, String worldName, String channelName) {
        // Add player to the channel in the given world
        System.out.println("Creating player " + username + " in world " + worldName + " on channel " + channelName);
        // Perform the logic to add the player to the appropriate channel
        return true;  // Return true if successful
    }
}
