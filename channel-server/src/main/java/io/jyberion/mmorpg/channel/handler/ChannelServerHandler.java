package io.jyberion.mmorpg.channel.handler;

import io.jyberion.mmorpg.channel.model.GameWorld;
import io.jyberion.mmorpg.common.message.*;
import io.jyberion.mmorpg.common.model.*;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelServerHandler extends SimpleChannelInboundHandler<Message> {
    private final ChannelInfo channelInfo;
    private static final ConcurrentHashMap<String, PlayerSession> sessions = new ConcurrentHashMap<>();
    private static final GameWorld gameWorld = new GameWorld(); // In-memory game state

    public ChannelServerHandler(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof ChannelAuthenticationMessage) {
            handleAuthentication(ctx, (ChannelAuthenticationMessage) msg);
        } else if (msg instanceof PlayerActionMessage) {
            handlePlayerAction(ctx, (PlayerActionMessage) msg);
        } else {
            // Handle other message types
        }
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
            // Process the action
            synchronized (gameWorld) {
                gameWorld.processPlayerAction(username, msg);
            }
        } else {
            // Session not found, close connection
            ctx.close();
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
        // Handle player disconnection
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
            // Update player count
            channelInfo.setCurrentPlayers(channelInfo.getCurrentPlayers() - 1);
            // Remove player from game world
            gameWorld.removePlayer(disconnectedUser);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
