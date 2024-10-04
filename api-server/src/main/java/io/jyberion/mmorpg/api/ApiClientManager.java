package io.jyberion.mmorpg.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ApiClientManager {

    private Map<String, Channel> serverConnections = new HashMap<>();

    public ApiClientManager() {
        connectToServers();
    }

    private void connectToServers() {
        connectToServer("CHAT", "localhost", 9002);
        connectToServer("WORLD", "localhost", 9003);
        connectToServer("LOGIN", "localhost", 9001);
    }

    private void connectToServer(String serverName, String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ApiClientInitializer());

            ChannelFuture future = bootstrap.connect(host, port).sync();
            serverConnections.put(serverName, future.channel());
            System.out.println("Connected to " + serverName + " server at " + host + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommandToServer(String serverName, String command) {
        Channel serverChannel = serverConnections.get(serverName);
        if (serverChannel != null && serverChannel.isActive()) {
            serverChannel.writeAndFlush(command + "\n");
            System.out.println("Command sent to " + serverName + " server: " + command);
        } else {
            System.out.println(serverName + " server is not connected.");
        }
    }
}
