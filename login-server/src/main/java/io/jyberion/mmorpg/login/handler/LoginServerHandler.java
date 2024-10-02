package io.jyberion.mmorpg.login.handler;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.message.*;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.jyberion.mmorpg.login.WorldServerClient;
import io.jyberion.mmorpg.login.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoginServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(LoginServerHandler.class);
    private final UserService userService = new UserService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        ctx.executor().execute(() -> {
            try {
                if (msg instanceof LoginRequestMessage) {
                    handleLoginRequest(ctx, (LoginRequestMessage) msg);
                } else {
                    logger.warn("Received unknown message type: {}", msg.getType());
                }
            } catch (Exception e) {
                logger.error("Error processing message", e);
                ctx.close();
            }
        });
    }

    private void handleLoginRequest(ChannelHandlerContext ctx, LoginRequestMessage request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();

            if (userService.authenticateUser(username, password)) {
                long expiration = Long.parseLong(ConfigLoader.get("jwt.expiration"));
                String token = TokenUtil.generateToken(username, expiration);

                WorldServerClient worldClient = new WorldServerClient();
                CompletableFuture<List<ChannelInfo>> channelsFuture = worldClient.getAvailableChannels();

                channelsFuture.thenAccept(channels -> {
                    LoginResponseMessage response = new LoginResponseMessage(true, token, "Login successful.", channels);
                    ctx.writeAndFlush(response);
                }).exceptionally(ex -> {
                    logger.error("Failed to retrieve channels from World Server", ex);
                    LoginResponseMessage response = new LoginResponseMessage(false, null, "Unable to retrieve channels.", null);
                    ctx.writeAndFlush(response);
                    return null;
                });
            } else {
                LoginResponseMessage response = new LoginResponseMessage(false, null, "Invalid credentials.", null);
                ctx.writeAndFlush(response);
            }
        } catch (Exception e) {
            logger.error("Error processing login request", e);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in LoginServerHandler", cause);
        ctx.close();
    }
}
