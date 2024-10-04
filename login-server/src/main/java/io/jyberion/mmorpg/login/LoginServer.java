package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.login.service.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServer {

    private static final Logger logger = LoggerFactory.getLogger(LoginServer.class);
    private final int port;

    public LoginServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException, SSLException {
        UserService userService = new UserService();
        if (!userService.testDatabaseConnection()) {
            logger.error("Failed to connect to the database. Shutting down.");
            return;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // Accept connections
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Handle data

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new LoginServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            logger.info("Login Server started on port {}", port);
            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            userService.shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException, SSLException {
        new LoginServer(9001).start();
    }
}
