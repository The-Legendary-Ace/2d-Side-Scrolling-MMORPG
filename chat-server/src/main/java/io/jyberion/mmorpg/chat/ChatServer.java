package io.jyberion.mmorpg.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatServer {

    private final int port;
    private ChatServerHandler chatServerHandler;
    private ScheduledExecutorService scheduler; // For scheduling announcements

    public ChatServer(int port) {
        this.port = port;
        this.scheduler = Executors.newScheduledThreadPool(1); // Scheduler with 1 thread
    }

    public void start() throws InterruptedException {
        chatServerHandler = new ChatServerHandler();  // Initialize the handler

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1); // Handles incoming connections
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // Handles the actual messaging

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(chatServerHandler); // Pass handler to pipeline
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Chat Server started on port " + port);

            // Schedule a periodic announcement every 10 minutes
            scheduleAnnouncements();

            // Block until the server socket is closed.
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            scheduler.shutdown(); // Shut down the scheduler when the server is stopped
        }
    }

    // Schedule announcements at fixed intervals
    private void scheduleAnnouncements() {
        scheduler.scheduleAtFixedRate(() -> {
            if (chatServerHandler != null) {
                chatServerHandler.sendGlobalAnnouncement("Scheduled Announcement: Don't forget to visit the in-game shop!");
            }
        }, 0, 10, TimeUnit.MINUTES);  // Announce every 10 minutes
    }

    // Non-static method to trigger announcements manually
    public void announce(String message) {
        if (chatServerHandler != null) {
            chatServerHandler.sendGlobalAnnouncement(message);
        } else {
            System.err.println("Cannot send announcement. Chat server is not running.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ChatServer chatServer = new ChatServer(9002);
        chatServer.start();
    }
}
