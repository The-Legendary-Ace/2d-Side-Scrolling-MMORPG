package io.jyberion.mmorpg.login.handler;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.message.LoginRequestMessage;
import io.jyberion.mmorpg.common.message.LoginResponseMessage;
import io.jyberion.mmorpg.common.message.Message;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.jyberion.mmorpg.login.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoginServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(LoginServerHandler.class);
    private final UserService userService = new UserService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof LoginRequestMessage) {
            handleLoginRequest(ctx, (LoginRequestMessage) msg);
        } else {
            logger.warn("Received unknown message type: {}", msg.getType());
        }
    }

    private void handleLoginRequest(ChannelHandlerContext ctx, LoginRequestMessage request) {
        ctx.executor().execute(() -> {
            try {
                String username = request.getUsername();
                String password = request.getPassword();

                // Authenticate user
                if (userService.authenticateUser(username, password)) {
                    // Generate token
                    long expiration = Long.parseLong(ConfigLoader.get("jwt.expiration"));
                    String token = TokenUtil.generateToken(username, expiration);

                    // Fetch channel list
                    List<ChannelInfo> channels = getAvailableChannels();

                    // Send success response
                    LoginResponseMessage response = new LoginResponseMessage(true, token, "Login successful.", channels);
                    ctx.writeAndFlush(response);
                } else {
                    // Send failure response
                    LoginResponseMessage response = new LoginResponseMessage(false, null, "Invalid credentials.", null);
                    ctx.writeAndFlush(response);
                }
            } catch (Exception e) {
                logger.error("Error processing login request", e);
                ctx.close();
            }
        });
    }

    private List<ChannelInfo> getAvailableChannels() {
        // TODO: Implement actual channel retrieval logic
        // For now, return an empty list or mock data
        return List.of(); // Java 9+ syntax
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in LoginServerHandler", cause);
        ctx.close();
    }
}
