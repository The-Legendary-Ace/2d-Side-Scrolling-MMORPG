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

        // Set a strict limit on frame length
        int maxFrameLength = 10 * 1024 * 1024; // 10 MB max frame length

        pipeline.addLast(new LengthFieldBasedFrameDecoder(10 * 1024 * 1024, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));

        // Add your custom encoder and decoder
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());

        // Print the handlers in the pipeline for debugging purposes
        System.out.println("Current pipeline handlers: " + pipeline.names());

        System.out.println("Initializing channel for world: " + worldName);
    }
}
