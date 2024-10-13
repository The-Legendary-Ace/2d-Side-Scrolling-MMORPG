package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.login.handler.LoginServerHandler;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class LoginServer {

    public static void main(String[] args) throws Exception {
        // Create the Hibernate SessionFactory
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

        // Create event loop groups for handling incoming connections and channel traffic
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Create the server bootstrap to set up the server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // Create the pipeline for handling requests and responses
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4)); // Decoder for handling frames
                            pipeline.addLast(new LengthFieldPrepender(4)); // Encoder for frame length
                            pipeline.addLast(new MessageDecoder()); // Custom JSON decoder
                            pipeline.addLast(new MessageEncoder()); // Custom JSON encoder
                            pipeline.addLast(new LoginServerHandler(sessionFactory)); // Pass sessionFactory to handler
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });

            // Bind to port 8080 and start accepting connections
            ChannelFuture future = bootstrap.bind(8080).sync();
            System.out.println("Login Server is running on port 8080");

            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            // Gracefully shut down the event loop groups
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            sessionFactory.close();
        }
    }
}
