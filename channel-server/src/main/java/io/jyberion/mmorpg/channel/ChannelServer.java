package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.InputStream;
import java.util.Properties;

public class ChannelServer {
    private final int port;
    private final ChannelInfo channelInfo;

    public ChannelServer(int port, ChannelInfo channelInfo) {
        this.port = port;
        this.channelInfo = channelInfo;
    }

    public void start() throws InterruptedException {
        // Event loop groups for handling channels
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1); // Accepts connections
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // Handles traffic

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelServerInitializer(channelInfo))
                     .option(ChannelOption.SO_BACKLOG, 128)
                     .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Channel Server started on port " + port);

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("Channel Server shut down");
        }
    }

    public static void main(String[] args) {
        // Load configuration file from classpath
        try (InputStream input = ChannelServer.class.getClassLoader().getResourceAsStream("channel.properties")) {
            if (input == null) {
                throw new RuntimeException("channel.properties not found in classpath");
            }
            Properties properties = new Properties();
            properties.load(input);
            properties.forEach((key, value) -> ConfigLoader.loadProperty((String) key, (String) value));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            String channelName = ConfigLoader.get("channel.name");
            String channelAddress = ConfigLoader.get("channel.address");
            String channelPortStr = ConfigLoader.get("channel.port");
            String channelMaxPlayersStr = ConfigLoader.get("channel.maxPlayers");
            String worldServerHost = ConfigLoader.get("world.server.host");
            String worldServerPortStr = ConfigLoader.get("world.server.port");

            // Validate and parse the configuration values
            if (channelName == null || channelAddress == null || channelPortStr == null || channelMaxPlayersStr == null || worldServerHost == null || worldServerPortStr == null) {
                throw new IllegalArgumentException("Missing required configuration properties");
            }

            int channelPort = Integer.parseInt(channelPortStr);
            int channelMaxPlayers = Integer.parseInt(channelMaxPlayersStr);
            int worldServerPort = Integer.parseInt(worldServerPortStr);

            ChannelInfo channelInfo = new ChannelInfo(
                    channelName,
                    channelAddress,
                    channelPort,
                    0,
                    channelMaxPlayers
            );

            // Register with the World Server
            ChannelRegistrationClient registrationClient = new ChannelRegistrationClient(
                    worldServerHost, worldServerPort, channelInfo
            );

            try {
                registrationClient.register();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            // Start the Channel Server
            new ChannelServer(channelPort, channelInfo).start();

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            System.err.println("Configuration error: " + e.getMessage());
            System.exit(1);
        }
    }
}