package io.jyberion.mmorpg.chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ChatServerHandler chatServerHandler;

    public ChatServerInitializer(ChatServerHandler chatServerHandler) {
        this.chatServerHandler = chatServerHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ObjectEncoder(),
                chatServerHandler);
    }
}
