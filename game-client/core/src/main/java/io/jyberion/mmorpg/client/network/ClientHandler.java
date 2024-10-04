package io.jyberion.mmorpg.client.network;

import io.jyberion.mmorpg.common.message.LoginResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    private final ClientNetworkManager networkManager;
    private Consumer<LoginResponseMessage> loginResponseCallback;

    public ClientHandler(ClientNetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void setLoginResponseCallback(Consumer<LoginResponseMessage> callback) {
        this.loginResponseCallback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof LoginResponseMessage) {
            if (loginResponseCallback != null) {
                loginResponseCallback.accept((LoginResponseMessage) msg);
                loginResponseCallback = null; // Reset callback after use
            }
        }
        // Handle other message types if necessary
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
