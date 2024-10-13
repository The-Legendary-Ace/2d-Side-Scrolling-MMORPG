package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.message.ChannelInfoMessage;  // Assume this is a message type sent by the channel server
import io.jyberion.mmorpg.common.message.ChannelRegistrationMessage;
import io.jyberion.mmorpg.common.message.ChannelRegistrationResponse;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelDetailsClientHandler extends SimpleChannelInboundHandler<ChannelInfoMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ChannelDetailsClientHandler.class);

    private final ChannelHandlerContext worldServerCtx;
    private final ChannelRegistrationMessage registrationMessage;

    public ChannelDetailsClientHandler(ChannelHandlerContext worldServerCtx, ChannelRegistrationMessage registrationMessage) {
        this.worldServerCtx = worldServerCtx;
        this.registrationMessage = registrationMessage;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChannelInfoMessage msg) throws Exception {
        logger.info("Received channel details from channel server: {}", msg);

        // Build ChannelInfo from the message received from the channel server
        ChannelInfo newChannelInfo = new ChannelInfo(
                registrationMessage.getWorldId(),  // Add logic to get world ID
                registrationMessage.getChannelName(),
                registrationMessage.getHost(),
                registrationMessage.getPort(),
                msg.getCurrentPlayers(),  // Fetched from the channel server
                msg.getMaxPlayers(),      // Fetched from the channel server
                msg.getStatus()           // Fetched from the channel server
        );

        // Send registration response to the channel
        ChannelRegistrationResponse response = new ChannelRegistrationResponse(true);
        worldServerCtx.writeAndFlush(response).addListener((future) -> {
            if (future.isSuccess()) {
                logger.info("Registration successful for channel: {}", newChannelInfo.getChannelName());
            } else {
                logger.error("Failed to send registration response for channel: {}", newChannelInfo.getChannelName());
            }
        });

        // Register the channel on the world server (you might want to persist it)
        // registeredChannels.add(newChannelInfo); // Add this to the world server's list of channels.
        ctx.close();  // Close the connection after receiving the info
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error while communicating with the channel server", cause);
        ctx.close();
    }
}
