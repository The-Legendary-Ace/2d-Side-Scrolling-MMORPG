package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelRegistrationClient {

    private static final Logger logger = LoggerFactory.getLogger(ChannelRegistrationClient.class);
    private final String worldServerHost;
    private final int worldServerPort;
    private final ChannelInfo channelInfo;

    public ChannelRegistrationClient(String worldServerHost, int worldServerPort, ChannelInfo channelInfo) {
        this.worldServerHost = worldServerHost;
        this.worldServerPort = worldServerPort;
        this.channelInfo = channelInfo;
    }

    public void register() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            logger.debug("Initializing ChannelRegistrationHandler pipeline...");
                            ch.pipeline().addLast(new ChannelRegistrationHandler(channelInfo));
                        }
                    });

            logger.info("Connecting to world server at {}:{}", worldServerHost, worldServerPort);
            ChannelFuture f = b.connect(worldServerHost, worldServerPort).sync();
            logger.info("Connected to world server, awaiting response...");
            f.channel().closeFuture().sync();
        } finally {
            logger.info("Shutting down ChannelRegistrationClient...");
            group.shutdownGracefully();
        }
    }
}
