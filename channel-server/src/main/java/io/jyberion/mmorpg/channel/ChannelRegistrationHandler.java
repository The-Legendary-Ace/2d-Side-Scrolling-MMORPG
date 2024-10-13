package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.message.ChannelRegistrationMessage;
import io.jyberion.mmorpg.common.message.ChannelRegistrationResponse;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelRegistrationHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ChannelRegistrationHandler.class);
    private final ChannelInfo channelInfo;
    private final String worldId; // Add worldId to pass in the message

    public ChannelRegistrationHandler(ChannelInfo channelInfo, String worldId) { // Pass worldId as a parameter
        this.channelInfo = channelInfo;
        this.worldId = worldId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Channel active, registering channel to World Server...");
        logger.debug("Channel information: {}", channelInfo);

        // Convert ChannelInfo to a ChannelRegistrationMessage to properly encode
        ChannelRegistrationMessage registrationMessage = new ChannelRegistrationMessage(
                worldId, // Provide the worldId here
                channelInfo.getChannelName(),
                channelInfo.getHost(),
                channelInfo.getPort()
        );

        // Send the serialized registration message
        ctx.writeAndFlush(registrationMessage);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ChannelRegistrationResponse) {
            ChannelRegistrationResponse response = (ChannelRegistrationResponse) msg;
            if (response.isSuccess()) {
                logger.info("Successfully registered with World Server.");
                // Proceed with further initialization if necessary
            } else {
                logger.error("Failed to register with World Server: {}", response.getErrorMessage());
                ctx.close();
            }
        } else {
            logger.warn("Received unexpected message type: {}", msg.getClass());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in ChannelRegistrationHandler", cause);
        ctx.close();
    }
}
