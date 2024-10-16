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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class LoginServerHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    private static final Logger logger = LoggerFactory.getLogger(LoginServerHandler.class);
    private final UserService userService = new UserService();
    private final SessionFactory sessionFactory;

    public LoginServerHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;  // Inject sessionFactory
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) {
        logger.info("Received login request for username: {}", msg.getUsername());

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

                    // Get the channels and world name
                    List<LoginResponseMessage.WorldWithChannels> worldsWithChannels = getWorldsAndChannelsFromWorldServers();

                    if (worldsWithChannels == null || worldsWithChannels.isEmpty()) {
                        logger.error("No channels available for any world server.");
                        response = new LoginResponseMessage(
                                "LOGIN_RESPONSE", false, null, "No channels available. Please try again later.", null, false, null
                        );
                    } else {
                        // Send back the world and channel data to the client
                        logger.info("Returning worlds and channels data for login");
                        response = new LoginResponseMessage(
                                "LOGIN_RESPONSE", true, sessionId, "Login successful", worldsWithChannels, false, null
                        );
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

    // Communicate with the world servers to retrieve available channels
    private CompletableFuture<List<ChannelInfo>> communicateWithWorldServer(WorldInfo worldInfo) {
        CompletableFuture<List<ChannelInfo>> future = new CompletableFuture<>();
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
                                    future.complete(msg.getChannels());
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
            logger.error("Error communicating with World Server: {}", worldInfo.getWorldName(), e);
            future.completeExceptionally(e);
        } finally {
            group.shutdownGracefully();
        }

        return future;
    }

    // Retrieve the list of available worlds and their channels
    private List<LoginResponseMessage.WorldWithChannels> getWorldsAndChannelsFromWorldServers() {
        List<WorldInfo> worldServers = getAvailableWorldServers();

        if (worldServers.isEmpty()) {
            logger.error("No world servers available.");
            return new ArrayList<>();
        }

        List<LoginResponseMessage.WorldWithChannels> worldsWithChannels = new ArrayList<>();
        for (WorldInfo world : worldServers) {
            List<ChannelInfo> channels = communicateWithWorldServer(world).join();
            if (channels != null && !channels.isEmpty()) {
                logger.info("Channels found for world {}", world.getWorldName());
                worldsWithChannels.add(new LoginResponseMessage.WorldWithChannels(world.getWorldName(), channels));
            } else {
                logger.warn("No channels available for world: {}", world.getWorldName());
            }
        }
        return worldsWithChannels;
    }

    // Retrieve the available world servers from the database
    private List<WorldInfo> getAvailableWorldServers() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Query the world servers from the DB
            List<WorldInfo> worldServers = session.createQuery("FROM WorldInfo", WorldInfo.class).list();
            if (worldServers.isEmpty()) {
                logger.error("No world servers found in the database.");
            } else {
                logger.info("Found {} world servers in the database.", worldServers.size());
            }

            session.getTransaction().commit();
            return worldServers;
        } catch (PersistenceException e) {
            logger.error("Error retrieving world servers from the database", e);
            return new ArrayList<>();
        }
    }
}
