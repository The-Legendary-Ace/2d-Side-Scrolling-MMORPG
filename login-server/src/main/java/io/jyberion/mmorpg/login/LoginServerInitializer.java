package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.login.handler.LoginServerHandler;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class LoginServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4), // Ensure consistent frame decoder
                new LengthFieldPrepender(4),
                new MessageDecoder(), // Custom JSON decoder
                new MessageEncoder(), // Custom JSON encoder
                new LoginServerHandler() // Handle login requests
        );
        System.out.println("Client connection initialized.");
    }
}
