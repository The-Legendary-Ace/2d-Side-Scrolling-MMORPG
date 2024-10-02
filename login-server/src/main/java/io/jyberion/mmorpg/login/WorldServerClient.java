package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.message.*;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldServerClient {
    private final String worldServerHost;
    private final int worldServerPort;

    public WorldServerClient() {
        this.worldServerHost = ConfigLoader.get("world.server.host");
        this.worldServerPort = Integer.parseInt(ConfigLoader.get("world.server.port"));
    }

    public CompletableFuture<List<ChannelInfo>> getAvailableChannels() {
        CompletableFuture<List<ChannelInfo>> future = new CompletableFuture<>();

        NioEventLoopGroup group = new NioEventLoopGroup();
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
                                 }
                             }

                             @Override
                             public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                 future.completeExceptionally(cause);
                                 ctx.close();
                             }
                         });
                     }
                 });

        bootstrap.connect(worldServerHost, worldServerPort).addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                future1.channel().writeAndFlush(new GetAvailableChannelsMessage());
            } else {
                future.completeExceptionally(future1.cause());
            }
        });

        return future.whenComplete((result, throwable) -> group.shutdownGracefully());
    }
}
