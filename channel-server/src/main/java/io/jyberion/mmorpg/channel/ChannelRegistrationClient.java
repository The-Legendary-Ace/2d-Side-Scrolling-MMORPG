package io.jyberion.mmorpg.channel;

import io.jsonwebtoken.security.Message;
import io.jyberion.mmorpg.common.message.ChannelRegistrationMessage;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChannelRegistrationClient {
    private final String worldServerHost;
    private final int worldServerPort;
    private final ChannelInfo channelInfo;

    public ChannelRegistrationClient(String worldServerHost, int worldServerPort, ChannelInfo channelInfo) {
        this.worldServerHost = worldServerHost;
        this.worldServerPort = worldServerPort;
        this.channelInfo = channelInfo;
    }

    public void register() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .handler(new ChannelInitializer<Channel>() {
                         @Override
                         protected void initChannel(Channel ch) {
                             ChannelPipeline pipeline = ch.pipeline();
                             pipeline.addLast(new MessageEncoder());
                             pipeline.addLast(new MessageDecoder());
                             pipeline.addLast(new SimpleChannelInboundHandler<Message>() {
                                 @Override
                                 protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                     // Handle responses if needed
                                 }

                                 @Override
                                 public void channelActive(ChannelHandlerContext ctx) {
                                     ctx.writeAndFlush(new ChannelRegistrationMessage(channelInfo));
                                 }
                             });
                         }
                     });

            ChannelFuture future = bootstrap.connect(worldServerHost, worldServerPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
