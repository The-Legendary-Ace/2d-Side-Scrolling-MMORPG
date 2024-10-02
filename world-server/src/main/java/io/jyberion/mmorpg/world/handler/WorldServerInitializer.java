package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

public class WorldServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;

    public WorldServerInitializer() throws SSLException {
        boolean sslEnabled = Boolean.parseBoolean(ConfigLoader.get("ssl.enabled"));
        if (sslEnabled) {
            String certChainFilePath = ConfigLoader.get("ssl.certChainFile");
            String privateKeyFilePath = ConfigLoader.get("ssl.privateKeyFile");
            sslContext = SslContextBuilder.forServer(new File(certChainFilePath), new File(privateKeyFilePath)).build();
        } else {
            sslContext = null;
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }

        // Add your handlers here
        pipeline.addLast(new WorldServerHandler());
    }
}
