package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.message.MessageDecoder;
import io.jyberion.mmorpg.common.message.MessageEncoder;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.IOException;

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
        try {
            ConfigLoader.load("channel-server.properties");

            ChannelInfo channelInfo = new ChannelInfo(
                    ConfigLoader.get("channel.name"),
                    ConfigLoader.get("channel.address"),
                    Integer.parseInt(ConfigLoader.get("channel.port")),
                    0,
                    Integer.parseInt(ConfigLoader.get("channel.maxPlayers"))
            );

            String worldServerHost = ConfigLoader.get("world.server.host");
            int worldServerPort = Integer.parseInt(ConfigLoader.get("world.server.port"));

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
            int port = Integer.parseInt(ConfigLoader.get("channel.port"));
            new ChannelServer(port, channelInfo).start();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
