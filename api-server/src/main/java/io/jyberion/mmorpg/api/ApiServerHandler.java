package io.jyberion.mmorpg.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ApiServerHandler extends SimpleChannelInboundHandler<String> {

    private final ApiClientManager clientManager = new ApiClientManager();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) throws Exception {
        // Process incoming API commands and forward to appropriate servers
        if (command.startsWith("CHAT:")) {
            clientManager.sendCommandToServer("CHAT", command.substring(5));
        } else if (command.startsWith("WORLD:")) {
            clientManager.sendCommandToServer("WORLD", command.substring(6));
        } else if (command.startsWith("LOGIN:")) {
            clientManager.sendCommandToServer("LOGIN", command.substring(6));
        } else {
            ctx.writeAndFlush("Unknown command: " + command + "\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
