package io.jyberion.mmorpg.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // Accept connections
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Handle data

        try {
            ServerBootstrap b = new ServerBootstrap();
            ChatServerHandler chatServerHandler = new ChatServerHandler();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer(chatServerHandler))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            logger.info("Chat Server started on port {}", port);
            ChannelFuture f = b.bind(port).sync();

            // Example of sending a global announcement
            chatServerHandler.sendGlobalAnnouncement("Server is starting up!");

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatServer(9002).start();
    }
}
