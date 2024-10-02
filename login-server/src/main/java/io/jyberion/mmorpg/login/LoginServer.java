package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.login.handler.LoginServerInitializer;
import io.jyberion.mmorpg.login.service.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServer {
    private static final Logger logger = LoggerFactory.getLogger(LoginServer.class);

    public static void main(String[] args) {
        try {
            ConfigLoader.load("login-server.properties");
            new LoginServer().start();
        } catch (Exception e) {
            logger.error("Failed to start Login Server", e);
            System.exit(1); // Exit the application with an error code
        }
    }

    public void start() throws Exception {
        int port = Integer.parseInt(ConfigLoader.get("server.port"));

        // Initialize UserService and test database connection
        UserService userService = new UserService();
        if (!userService.testDatabaseConnection()) {
            throw new IllegalStateException("Failed to connect to the database. Server will not start.");
        }

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1); // Accepts connections
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // Handles traffic

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class) // Use NIO selector-based implementation
                     .childHandler(new LoginServerInitializer())
                     .option(ChannelOption.SO_BACKLOG, 128) // Number of connections queued
                     .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync(); // Bind and start to accept incoming connections
            logger.info("Login Server started on port {}", port);

            future.channel().closeFuture().sync(); // Wait until the server socket is closed
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("Login Server shut down");
        }
    }
}
