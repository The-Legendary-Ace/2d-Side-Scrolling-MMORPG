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
    private final UserService userService = new UserService(); // Service for handling user authentication

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

    // Handle the login request asynchronously
    private void handleLoginRequest(ChannelHandlerContext ctx, LoginRequestMessage request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // Authenticate the user asynchronously
        CompletableFuture<Boolean> authFuture = userService.authenticateUserAsync(username, password);

        authFuture.thenCompose(authenticated -> {
            if (authenticated) {
                // If the user is authenticated, generate JWT token
                long expiration = Long.parseLong(ConfigLoader.get("jwt.expiration"));
                String token = TokenUtil.generateToken(username, expiration);

                // Fetch available channels from the world server asynchronously
                WorldServerClient worldClient = new WorldServerClient();
                return worldClient.getAvailableChannels()
                        .thenApply(channels -> new LoginResponseMessage(true, token, "Login successful.", channels));
            } else {
                // If authentication fails, send failure response
                return CompletableFuture.completedFuture(
                        new LoginResponseMessage(false, null, "Invalid credentials.", null)
                );
            }
        }).thenAccept(response -> {
            // Send the login response
            ctx.writeAndFlush(response);
        }).exceptionally(ex -> {
            // Handle any errors during authentication or channel retrieval
            logger.error("Error during login process", ex);
            LoginResponseMessage response = new LoginResponseMessage(false, null, "Error processing login request.", null);
            ctx.writeAndFlush(response);
            return null;
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in LoginServerHandler", cause);
        ctx.close();
    }
}
