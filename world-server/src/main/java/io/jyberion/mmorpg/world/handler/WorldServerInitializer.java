package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class WorldServerInitializer extends ChannelInitializer<SocketChannel> {

    private final String worldName;

    public WorldServerInitializer(String worldName) {
        this.worldName = worldName;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Outbound handlers (encoders)
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("messageEncoder", new MessageEncoder());

        // Inbound handlers (decoders)
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        pipeline.addLast("messageDecoder", new MessageDecoder());

        // Business logic handler
        pipeline.addLast("handler", new WorldServerHandler());

        // Debugging
        System.out.println("Current pipeline handlers: " + pipeline.names());
        System.out.println("Initializing channel for world: " + worldName);
    }
}
