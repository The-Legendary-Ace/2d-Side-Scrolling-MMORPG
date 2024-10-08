package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.channel.handler.ChannelServerHandler;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ChannelServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ChannelInfo channelInfo;

    public ChannelServerInitializer(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Add LengthFieldBasedFrameDecoder to handle large frames
        pipeline.addLast(new LengthFieldBasedFrameDecoder(10 * 1024 * 1024, 0, 4, 0, 4));

        // Add encoders and decoders
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());

        // Add your handler
        pipeline.addLast(new ChannelServerHandler(channelInfo));
    }
}