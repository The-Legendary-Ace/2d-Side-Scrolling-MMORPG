package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.message.AvailableChannelsResponseMessage;
import io.jyberion.mmorpg.common.message.ChannelHeartbeatMessage;
import io.jyberion.mmorpg.common.message.ChannelRegistrationMessage;
import io.jyberion.mmorpg.common.message.GetAvailableChannelsMessage;
import io.jyberion.mmorpg.common.message.Message;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.world.ChannelManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class WorldServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(WorldServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        // Handle incoming messages asynchronously
        ctx.executor().execute(() -> {
            try {
                if (msg instanceof ChannelRegistrationMessage) {
                    handleChannelRegistration((ChannelRegistrationMessage) msg);
                } else if (msg instanceof GetAvailableChannelsMessage) {
                    handleGetAvailableChannels(ctx);
                } else if (msg instanceof ChannelHeartbeatMessage) {
                    handleChannelHeartbeat((ChannelHeartbeatMessage) msg);
                } else {
                    logger.warn("Received unknown message type: {}", msg.getType());
                }
            } catch (Exception e) {
                logger.error("Error processing message", e);
                ctx.close();
            }
        });
    }

    private void handleChannelRegistration(ChannelRegistrationMessage msg) {
        ChannelInfo channelInfo = msg.getChannelInfo();
        ChannelManager.registerChannel(channelInfo);
        logger.info("Registered new channel: {}", channelInfo.getName());
    }

    private void handleGetAvailableChannels(ChannelHandlerContext ctx) {
        Collection<ChannelInfo> channels = ChannelManager.getAvailableChannels();
        AvailableChannelsResponseMessage response = new AvailableChannelsResponseMessage(new ArrayList<>(channels));
        ctx.writeAndFlush(response);
    }

    private void handleChannelHeartbeat(ChannelHeartbeatMessage msg) {
        String channelName = msg.getChannelName();
        ChannelManager.updateHeartbeat(channelName);
        logger.debug("Received heartbeat from channel: {}", channelName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in WorldServerHandler", cause);
        ctx.close();
    }
}
