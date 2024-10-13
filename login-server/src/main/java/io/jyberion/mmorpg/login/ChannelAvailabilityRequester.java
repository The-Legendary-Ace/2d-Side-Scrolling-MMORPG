package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.common.message.ChannelAvailabilityRequest;
import io.jyberion.mmorpg.common.message.ChannelAvailabilityResponse;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChannelAvailabilityRequester {
    private final String worldServerHost;
    private final int worldServerPort;

    public ChannelAvailabilityRequester(String worldServerHost, int worldServerPort) {
        this.worldServerHost = worldServerHost;
        this.worldServerPort = worldServerPort;
    }

    public void requestChannelAvailability(ChannelAvailabilityRequest request) throws InterruptedException {
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
                            pipeline.addLast(new SimpleChannelInboundHandler<ChannelAvailabilityResponse>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ChannelAvailabilityResponse response) {
                                    // Handle the response
                                    System.out.println("Received channel availability response: " + response);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.connect(worldServerHost, worldServerPort).sync();
            future.channel().writeAndFlush(request);
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
