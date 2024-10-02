package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.channel.handler.ChannelServerHandler;
import io.jyberion.mmorpg.common.message.MessageDecoder;
import io.jyberion.mmorpg.common.message.MessageEncoder;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ChannelServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ChannelInfo channelInfo;

    public ChannelServerInitializer(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Add encoders and decoders
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());

        // Add your handler
        pipeline.addLast(new ChannelServerHandler(channelInfo));
    }
}
