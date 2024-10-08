

package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class WorldServerInitializer extends ChannelInitializer<SocketChannel> {

    private final String worldName;

    public WorldServerInitializer(String worldName) {
        this.worldName = worldName;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 4),
                new LengthFieldPrepender(4),
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null))
        );
        System.out.println("Initializing channel for world: " + worldName);
    }
}