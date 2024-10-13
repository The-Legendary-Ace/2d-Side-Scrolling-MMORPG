package io.jyberion.mmorpg.login.handler;

import io.jyberion.mmorpg.common.message.AvailableChannelsResponseMessage;
import io.jyberion.mmorpg.common.message.GetAvailableChannelsMessage;
import io.jyberion.mmorpg.common.message.LoginRequestMessage;
import io.jyberion.mmorpg.common.message.LoginResponseMessage;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.model.WorldInfo;
import io.jyberion.mmorpg.login.service.UserService;
import io.jyberion.mmorpg.login.service.UserService.BanStatus;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(LoginServerHandler.class);
    private final UserService userService = new UserService();

    public LoginServerHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) {
        logger.info("Received login request for username: {}", msg.getUsername());
        System.out.println("Received login request from client: " + msg.getUsername());

        String username = msg.getUsername();
        String password = msg.getPassword();

        try {
            boolean isValid = userService.authenticateUserAsync(username, password).join();
            BanStatus banStatus = userService.checkBanStatus(username).orElse(new BanStatus(false, null, null, null));

            LoginResponseMessage response;

            if (isValid) {
                if (banStatus.isBanned()) {
                    response = new LoginResponseMessage(
                            "LOGIN_RESPONSE", false, null, "Your account is banned.", null, true, banStatus.getBanReason()
                    );
                } else {
                    String sessionId = UUID.randomUUID().toString();
                    logger.info("Generated session ID for username {}: {}", username, sessionId);

                    List<ChannelInfo> channels = getAvailableChannelsFromWorldServer();

                    if (channels == null || channels.isEmpty()) {
                        logger.error("No channels available for the world server.");
                        response = new LoginResponseMessage(
                                "LOGIN_RESPONSE", false, null, "No channels available. Please try again later.", null, false, null
                        );
                    } else {
                        ChannelInfo selectedChannel = channels.get(0);
                        boolean playerCreated = createPlayerOnWorldServer(username, selectedChannel);

                        if (playerCreated) {
                            logger.info("Player {} created successfully on channel server.", username);
                            response = new LoginResponseMessage(
                                    "LOGIN_RESPONSE", true, sessionId, "Login successful", channels, false, null
                            );
                        } else {
                            logger.error("Failed to create player {} on channel server.", username);
                            response = new LoginResponseMessage(
                                    "LOGIN_RESPONSE", false, null, "Player creation failed. Please try again.", null, false, null
                            );
                        }
                    }
                }
            } else {
                response = new LoginResponseMessage(
                        "LOGIN_RESPONSE", false, null, "Invalid credentials", null, false, null
                );
                logger.info("Login failed for username: {}", username);
            }

            ctx.writeAndFlush(response);

        } catch (PersistenceException e) {
            logger.error("Database error during login", e);
            LoginResponseMessage errorResponse = new LoginResponseMessage(
                    "LOGIN_RESPONSE", false, null, "Unknown error occurred during login.", null, false, null
            );
            ctx.writeAndFlush(errorResponse);
        } catch (Exception e) {
            logger.error("Error during login process", e);
            LoginResponseMessage errorResponse = new LoginResponseMessage(
                    "LOGIN_RESPONSE", false, null, "An unexpected error occurred.", null, false, null
            );
            ctx.writeAndFlush(errorResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in LoginServerHandler", cause);
        ctx.close();
    }

    private CompletableFuture<ChannelInfo> communicateWithWorldServer(WorldInfo worldInfo) {
        CompletableFuture<ChannelInfo> future = new CompletableFuture<>();
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new MessageDecoder());
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new SimpleChannelInboundHandler<AvailableChannelsResponseMessage>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, AvailableChannelsResponseMessage msg) {
                                    if (msg.getAvailableChannels() != null && !msg.getAvailableChannels().isEmpty()) {
                                        future.complete(msg.getAvailableChannels().get(0));
                                    } else {
                                        future.complete(null);
                                    }
                                    ctx.close();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    future.completeExceptionally(cause);
                                    ctx.close();
                                }
                            });
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(worldInfo.getHost(), worldInfo.getPort()).sync();
            channelFuture.channel().writeAndFlush(new GetAvailableChannelsMessage());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            future.completeExceptionally(e);
        } finally {
            group.shutdownGracefully();
        }

        return future;
    }

    private List<WorldInfo> getAvailableWorldServers() {
        List<WorldInfo> worldServers = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Query the WorldInfo entity from the database
            worldServers = session.createQuery("FROM WorldInfo", WorldInfo.class).list();

            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("Failed to retrieve world servers from database", e);
        }

        return worldServers;
    }


    private List<ChannelInfo> getAvailableChannelsFromWorldServer() {
        List<WorldInfo> worldServers = getAvailableWorldServers();

        if (worldServers.isEmpty()) {
            logger.error("No world servers available.");
            return Collections.emptyList();
        }

        List<ChannelInfo> availableChannels = new ArrayList<>();
        for (WorldInfo world : worldServers) {
            ChannelInfo channel = communicateWithWorldServer(world).join();
            if (channel != null) {
                availableChannels.add(channel);
            }
        }
        return availableChannels;
    }

    private boolean createPlayerOnWorldServer(String username, ChannelInfo selectedChannel) {
        // Implement player creation logic
        return false;
    }
}
