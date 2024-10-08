package io.jyberion.mmorpg.login.handler;

import io.jyberion.mmorpg.common.message.LoginRequestMessage;
import io.jyberion.mmorpg.common.message.LoginResponseMessage;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.rpc.RPCClient;
import io.jyberion.mmorpg.login.service.UserService;
import io.jyberion.mmorpg.login.service.UserService.BanStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;

public class LoginServerHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(LoginServerHandler.class);
    private final UserService userService = new UserService();
    private final RPCClient rpcClient = new RPCClient();  // RPC Client for creating players and getting channel list

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) {
        logger.info("Received login request for username: {}", msg.getUsername());
        System.out.println("Received login request from client: " + msg.getUsername());

        String username = msg.getUsername();
        String password = msg.getPassword();

        try {
            // Validate username and password asynchronously
            boolean isValid = userService.authenticateUserAsync(username, password).join();
            BanStatus banStatus = userService.checkBanStatus(username).orElse(new BanStatus(false, null, null, null));

            LoginResponseMessage response;

            if (isValid) {
                if (banStatus.isBanned()) {
                    response = new LoginResponseMessage(
                            false, null, "Your account is banned.", null, true, banStatus.getBanReason()
                    );
                } else {
                    // Generate session ID
                    String sessionId = UUID.randomUUID().toString();
                    logger.info("Generated session ID for username {}: {}", username, sessionId);

                    // Get the list of available channels from the World Server
                    List<ChannelInfo> channels = rpcClient.getChannelList();
                    if (channels == null || channels.isEmpty()) {
                        logger.error("No channels available for the world server.");
                        response = new LoginResponseMessage(
                                false, null, "No channels available. Please try again later.", null, false, null
                        );
                    } else {
                        // Assume we use the first channel for the player creation
                        ChannelInfo selectedChannel = channels.get(0);

                        // Example RPC call to the world server to create the player
                        boolean playerCreated = rpcClient.callCreatePlayer(username, selectedChannel.getChannelName());

                        if (playerCreated) {
                            logger.info("Player {} created successfully on channel server.", username);
                            response = new LoginResponseMessage(
                                    true, sessionId, "Login successful", channels, false, null
                            );
                        } else {
                            logger.error("Failed to create player {} on channel server.", username);
                            response = new LoginResponseMessage(
                                    false, null, "Player creation failed. Please try again.", null, false, null
                            );
                        }
                    }
                }
            } else {
                response = new LoginResponseMessage(
                        false, null, "Invalid credentials", null, false, null
                );
                logger.info("Login failed for username: {}", username);
            }

            // Send the response back to the client
            ctx.writeAndFlush(response);

        } catch (PersistenceException e) {
            // Handle database errors (such as missing tables)
            logger.error("Database error during login", e);

            // Respond with a generic error message
            LoginResponseMessage errorResponse = new LoginResponseMessage(
                    false, null, "Unknown error occurred during login.", null, false, null
            );
            ctx.writeAndFlush(errorResponse);
        } catch (Exception e) {
            // Handle unexpected errors
            logger.error("Error during login process", e);
            LoginResponseMessage errorResponse = new LoginResponseMessage(
                    false, null, "An unexpected error occurred.", null, false, null
            );
            ctx.writeAndFlush(errorResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in LoginServerHandler", cause);
        ctx.close();
    }
}
