package io.jyberion.mmorpg.client.network;

import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final ClientNetworkManager networkManager;

    public ClientInitializer(ClientNetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4), // Ensure consistent frame decoder
                new LengthFieldPrepender(4),
                new MessageDecoder(), // Custom JSON decoder
                new MessageEncoder(), // Custom JSON encoder
                new ClientHandler(networkManager)
        );
    }
}
