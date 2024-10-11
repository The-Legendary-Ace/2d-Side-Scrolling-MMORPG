package io.jyberion.mmorpg.chat;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             ChannelPipeline p = ch.pipeline();
                             p.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4)); // Adjusted max frame length to 1MB
                             // Add other handlers here
                              p.addLast(new ChannelInboundHandlerAdapter() {
                                  @Override
                                  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                      cause.printStackTrace();
                                      ctx.close();
                                  }
                              });
                         }
                     })
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
