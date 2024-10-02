package io.jyberion.mmorpg.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.HashMap;
import java.util.Map;

public class ApiServer {

    private final String chatServerHost;
    private final int chatServerPort;
    private final String loginServerHost;
    private final int loginServerPort;
    private final String worldServerHost;
    private final int worldServerPort;

    private Map<String, Channel> serverConnections = new HashMap<>();

    public ApiServer(String chatHost, int chatPort, String loginHost, int loginPort, String worldHost, int worldPort) {
        this.chatServerHost = chatHost;
        this.chatServerPort = chatPort;
        this.loginServerHost = loginHost;
        this.loginServerPort = loginPort;
        this.worldServerHost = worldHost;
        this.worldServerPort = worldPort;
    }

    // Start the API server and connect to the game servers
    public void start() throws InterruptedException {
        // Connect to the chat server, login server, and world server
        connectToServers();
    }

    // Connect to internal game servers (chat, login, world)
    private void connectToServers() throws InterruptedException {
        connectToServer("CHAT", chatServerHost, chatServerPort);
        connectToServer("LOGIN", loginServerHost, loginServerPort);
        connectToServer("WORLD", worldServerHost, worldServerPort);
    }

    // Generic method to connect to a server
    private void connectToServer(String serverName, String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder(), new StringDecoder());
                        }
                    });

            // Connect to the server
            ChannelFuture future = bootstrap.connect(host, port).sync();
            serverConnections.put(serverName, future.channel());
            System.out.println("Connected to " + serverName + " server at " + host + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send a command to the appropriate game server
    public void sendCommandToServer(String serverName, String command) {
        Channel serverChannel = serverConnections.get(serverName);
        if (serverChannel != null && serverChannel.isActive()) {
            serverChannel.writeAndFlush(command + "\n");
            System.out.println("Command sent to " + serverName + " server: " + command);
        } else {
            System.out.println(serverName + " server is not connected.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Instantiate API Server and connect to all game servers
        ApiServer apiServer = new ApiServer("localhost", 9002, "localhost", 9001, "localhost", 9003);
        apiServer.start();

        // Simulate receiving an admin command and forwarding it to the chat server
        apiServer.sendCommandToServer("CHAT", "ANNOUNCE:Server maintenance in 10 minutes!");
    }
}
