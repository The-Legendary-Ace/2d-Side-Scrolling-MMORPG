package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.message.*;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.model.ChannelStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WorldServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(WorldServerHandler.class);

    // Make registeredChannels static so it's shared across all instances
    private static List<ChannelInfo> registeredChannels = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.debug("Received message of type: " + msg.getClass().getSimpleName());

        if (msg instanceof ChannelRegistrationMessage) {
            logger.debug("Handling ChannelRegistrationMessage...");
            handleChannelRegistration(ctx, (ChannelRegistrationMessage) msg);
        } else if (msg instanceof GetAvailableChannelsMessage) {
            logger.debug("Handling GetAvailableChannelsMessage...");
            handleGetAvailableChannels(ctx);  // Handle available channels request
        } else {
            logger.warn("Received unexpected message type: {}", msg.getClass().getSimpleName());
        }
    }

    private void handleChannelRegistration(ChannelHandlerContext ctx, ChannelRegistrationMessage registrationMessage) {
        logger.info("Received channel registration request from channel: {}", registrationMessage.getChannelName());

        if (isValidMessage(registrationMessage)) {
            String channelName = registrationMessage.getChannelName();
            String host = registrationMessage.getHost();
            int port = registrationMessage.getPort();
            int currentPlayers = registrationMessage.getCurrentPlayers();
            int maxPlayers = registrationMessage.getMaxPlayers();
            String status = registrationMessage.getStatus();

            logger.info("Channel {} is on host {}, port {} with players {}/{} and status: {}", 
                channelName, host, port, currentPlayers, maxPlayers, status);

            // Create a ChannelInfo object with the received registration details
            ChannelInfo channelInfo = new ChannelInfo(
                registrationMessage.getWorldId(),
                channelName,
                host,
                port,
                currentPlayers,
                maxPlayers,
                ChannelStatus.valueOf(status)
            );
            registeredChannels.add(channelInfo);  // Registering the channel

            // Log registered channels
            logger.debug("Current registered channels: {}", registeredChannels);

            // Send registration response
            ChannelRegistrationResponse response = new ChannelRegistrationResponse(true, "Registration successful.");
            ctx.writeAndFlush(response);
        } else {
            logger.error("Invalid channel registration message received: {}", registrationMessage);
            ChannelRegistrationResponse response = new ChannelRegistrationResponse(false, "Invalid channel registration details.");
            ctx.writeAndFlush(response);
        }
    }

    private void handleGetAvailableChannels(ChannelHandlerContext ctx) {
        logger.info("Received request for available channels.");

        if (registeredChannels == null || registeredChannels.isEmpty()) {
            logger.info("No channels are currently registered.");
        } else {
            logger.debug("Available channels: " + registeredChannels.size());
        }

        // Respond with the list of registered channels
        AvailableChannelsResponseMessage response = new AvailableChannelsResponseMessage(registeredChannels);
        ctx.writeAndFlush(response);
    }

    private boolean isValidMessage(ChannelRegistrationMessage msg) {
        logger.debug("Validating ChannelRegistrationMessage...");
        boolean valid = msg.getChannelName() != null && !msg.getChannelName().isEmpty()
                && msg.getHost() != null && !msg.getHost().isEmpty()
                && msg.getPort() > 0
                && msg.getCurrentPlayers() >= 0
                && msg.getMaxPlayers() >= 0
                && msg.getStatus() != null;
        logger.debug("Message validation result: {}", valid);
        return valid;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception caught in WorldServerHandler", cause);
        ctx.close();
    }
}
