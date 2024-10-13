package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.message.*;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldServerClient {
    private static final Logger logger = LoggerFactory.getLogger(WorldServerClient.class);

    private final String worldServerHost;
    private final int worldServerPort;

    public WorldServerClient() {
        this.worldServerHost = ConfigLoader.get("world.server.host");
        this.worldServerPort = Integer.parseInt(ConfigLoader.get("world.server.port"));
    }

    public CompletableFuture<List<ChannelInfo>> getAvailableChannels() {
        CompletableFuture<List<ChannelInfo>> future = new CompletableFuture<>();
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .handler(new ChannelInitializer<Channel>() {
                         @Override
                         protected void initChannel(Channel ch) {
                             ChannelPipeline pipeline = ch.pipeline();
                             pipeline.addLast(new MessageDecoder());
                             pipeline.addLast(new MessageEncoder());
                             pipeline.addLast(new SimpleChannelInboundHandler<Message>() {
                                 @Override
                                 protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                     if (msg instanceof AvailableChannelsResponseMessage) {
                                         AvailableChannelsResponseMessage response = (AvailableChannelsResponseMessage) msg;
                                         future.complete(response.getChannels());
                                         ctx.close();
                                     } else {
                                         logger.warn("Unexpected message type: {}", msg.getClass().getSimpleName());
                                         future.completeExceptionally(new IllegalStateException("Unexpected message type"));
                                     }
                                 }

                                 @Override
                                 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                     logger.error("Error during channel read", cause);
                                     future.completeExceptionally(cause);
                                     ctx.close();
                                 }
                             });
                         }
                     });

            bootstrap.connect(worldServerHost, worldServerPort).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info("Connected to world server at {}:{}", worldServerHost, worldServerPort);
                    future1.channel().writeAndFlush(new GetAvailableChannelsMessage());
                } else {
                    logger.error("Failed to connect to world server", future1.cause());
                    future.completeExceptionally(future1.cause());
                }
            });
        } catch (Exception e) {
            logger.error("Error initializing WorldServerClient", e);
            future.completeExceptionally(e);
        }

        return future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                logger.error("Error during world server communication", throwable);
            } else {
                logger.info("Successfully retrieved channel list");
            }
            group.shutdownGracefully();
        });
    }
}
