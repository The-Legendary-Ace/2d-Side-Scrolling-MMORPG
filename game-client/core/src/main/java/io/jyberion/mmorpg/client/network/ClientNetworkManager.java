package io.jyberion.mmorpg.client.network;

import io.jyberion.mmorpg.common.message.LoginRequestMessage;
import io.jyberion.mmorpg.common.message.LoginResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.Consumer;

public class ClientNetworkManager {

    private Channel channel;
    private EventLoopGroup group;

    public ClientNetworkManager() {
        group = new NioEventLoopGroup();
        connect();
    }

    private void connect() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer(this));

        bootstrap.connect("localhost", 9001).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                channel = future.channel();
            } else {
                System.err.println("Failed to connect to server.");
            }
        });
    }

    public void sendLoginRequest(LoginRequestMessage request, Consumer<LoginResponseMessage> callback) {
        if (channel != null && channel.isActive()) {
            channel.pipeline().get(ClientHandler.class).setLoginResponseCallback(callback);
            channel.writeAndFlush(request);
        } else {
            System.err.println("Channel is not active.");
        }
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
