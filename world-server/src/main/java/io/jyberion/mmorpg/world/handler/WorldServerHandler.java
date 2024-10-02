package io.jyberion.mmorpg.world.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.jyberion.mmorpg.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(WorldServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        // Handle incoming messages asynchronously
        ctx.executor().execute(() -> {
            try {
                // Process the message
            } catch (Exception e) {
                logger.error("Error processing message", e);
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in WorldServerHandler", cause);
        ctx.close();
    }
}
