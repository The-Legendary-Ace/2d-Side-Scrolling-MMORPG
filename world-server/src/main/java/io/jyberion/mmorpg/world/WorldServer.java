package io.jyberion.mmorpg.world;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.world.handler.WorldServerInitializer;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldServer {
    private static final Logger logger = LoggerFactory.getLogger(WorldServer.class);

    public static void main(String[] args) {
        try {
            // Load configuration
            ConfigLoader.load("world-server.properties");

            // Start Netty server
            int port = Integer.parseInt(ConfigLoader.get("server.port"));
            new WorldServer().start(port);
        } catch (Exception e) {
            logger.error("Failed to start World Server", e);
        }
    }

    public void start(int port) throws InterruptedException, SSLException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // Accepts incoming connections
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Handles the traffic

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class) // Use NIO selector-based implementation
                     .childHandler(new WorldServerInitializer())
                     .option(ChannelOption.SO_BACKLOG, 128) // Number of connections queued
                     .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync(); // Bind and start to accept incoming connections
            logger.info("World Server started on port {}", port);

            future.channel().closeFuture().sync(); // Wait until the server socket is closed
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("World Server shut down");
        }
    }
}
