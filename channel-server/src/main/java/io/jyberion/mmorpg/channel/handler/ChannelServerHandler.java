package io.jyberion.mmorpg.channel.handler;

import io.jyberion.mmorpg.channel.model.GameWorld;
import io.jyberion.mmorpg.common.message.*;
import io.jyberion.mmorpg.common.model.*;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServerHandler.class);

    private final ChannelInfo channelInfo;
    private static final ConcurrentHashMap<String, PlayerSession> sessions = new ConcurrentHashMap<>();
    private static final GameWorld gameWorld = new GameWorld(); // In-memory game state

    public ChannelServerHandler(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.debug("Received message of type: {}", msg.getClass().getSimpleName());

        if (msg instanceof ChannelRegistrationMessage) {
            handleChannelRegistration(ctx, (ChannelRegistrationMessage) msg);
        } else if (msg instanceof ChannelAuthenticationMessage) {
            handleAuthentication(ctx, (ChannelAuthenticationMessage) msg);
        } else if (msg instanceof PlayerActionMessage) {
            handlePlayerAction(ctx, (PlayerActionMessage) msg);
        } else {
            logger.warn("Unknown message type received: {}", msg.getClass().getSimpleName());
        }
    }

    // Handle the registration message, no need for a separate request for details
    private void handleChannelRegistration(ChannelHandlerContext ctx, ChannelRegistrationMessage registrationMessage) {
        logger.info("Handling ChannelRegistrationMessage for channel: {}", registrationMessage.getChannelName());

        // Process the channel registration, you can now use all the details from registrationMessage
        ChannelInfo channelInfo = new ChannelInfo(
            registrationMessage.getWorldId(),
            registrationMessage.getChannelName(),
            registrationMessage.getHost(),
            registrationMessage.getPort(),
            registrationMessage.getCurrentPlayers(),
            registrationMessage.getMaxPlayers(),
            ChannelStatus.valueOf(registrationMessage.getStatus())
        );

        // Further logic with the channelInfo object if needed...

        ctx.writeAndFlush(new ChannelRegistrationResponse(true, "Channel registered successfully."));
    }

    private void handleAuthentication(ChannelHandlerContext ctx, ChannelAuthenticationMessage msg) {
        String token = msg.getToken();
        String username = TokenUtil.validateToken(token);
        if (username != null) {
            PlayerSession session = new PlayerSession(username, ctx.channel());
            sessions.put(username, session);
            ctx.writeAndFlush(new ChannelAuthenticationResponse(true, "Authentication successful."));
            // Update player count
            channelInfo.setCurrentPlayers(channelInfo.getCurrentPlayers() + 1);
            // Initialize player in game world
            gameWorld.addPlayer(username);
        } else {
            ctx.writeAndFlush(new ChannelAuthenticationResponse(false, "Invalid token."));
            ctx.close();
        }
    }

    private void handlePlayerAction(ChannelHandlerContext ctx, PlayerActionMessage msg) {
        // Retrieve the player session
        String username = getUsernameByChannel(ctx.channel());
        if (username != null) {
            synchronized (gameWorld) {
                gameWorld.processPlayerAction(username, msg);
            }
        } else {
            ctx.close(); // Session not found, close connection
        }
    }

    private String getUsernameByChannel(Channel channel) {
        for (PlayerSession session : sessions.values()) {
            if (session.getChannel().equals(channel)) {
                return session.getUsername();
            }
        }
        return null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String disconnectedUser = null;
        for (String username : sessions.keySet()) {
            PlayerSession session = sessions.get(username);
            if (session.getChannel().equals(ctx.channel())) {
                sessions.remove(username);
                disconnectedUser = username;
                break;
            }
        }
        if (disconnectedUser != null) {
            channelInfo.setCurrentPlayers(channelInfo.getCurrentPlayers() - 1);
            gameWorld.removePlayer(disconnectedUser);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in ChannelServerHandler", cause);
        ctx.close();
    }
}
