package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.message.ChannelRegistrationMessage;
import io.jyberion.mmorpg.common.message.ChannelRegistrationResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelRegistrationHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ChannelRegistrationHandler.class);
    private final String worldId;
    private final String channelName;
    private final String host;
    private final int port;
    private final int currentPlayers;  // New field for current players
    private final int maxPlayers;      // New field for max players
    private final String status;       // New field for channel status

    public ChannelRegistrationHandler(String worldId, String channelName, String host, int port, int currentPlayers, int maxPlayers, String status) {
        this.worldId = worldId;
        this.channelName = channelName;
        this.host = host;
        this.port = port;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.status = status;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Channel active, registering channel to World Server...");

        // Construct ChannelRegistrationMessage with all required parameters
        ChannelRegistrationMessage registrationMessage = new ChannelRegistrationMessage(
                worldId, channelName, host, port, currentPlayers, maxPlayers, status
        );

        // Send the registration message to the World Server
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
