package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.message.ChannelRegistrationMessage;
import io.jyberion.mmorpg.common.message.ChannelRegistrationResponse;
import io.jyberion.mmorpg.common.message.Message;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorldServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(WorldServerHandler.class);

    // List of registered channels
    private List<ChannelInfo> registeredChannels;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof ChannelRegistrationMessage) {
            handleChannelRegistration(ctx, (ChannelRegistrationMessage) msg);
        } else {
            logger.warn("Received unexpected message type: {}", msg.getClass().getSimpleName());
        }
    }

    private void handleChannelRegistration(ChannelHandlerContext ctx, ChannelRegistrationMessage registrationMessage) {
        logger.info("Received channel registration message from: {}", registrationMessage.getChannelName());

        if (isValidMessage(registrationMessage)) {
            logger.info("Connecting to the channel server: {} at {}:{} to fetch additional details...",
                    registrationMessage.getChannelName(), registrationMessage.getHost(), registrationMessage.getPort());

            try {
                // Establish connection with the channel server to fetch additional info
                fetchChannelDetailsFromServer(registrationMessage, ctx);
            } catch (Exception e) {
                logger.error("Failed to register channel {} due to error", registrationMessage.getChannelName(), e);
                ChannelRegistrationResponse response = new ChannelRegistrationResponse(false, "Registration failed due to internal error.");
                ctx.writeAndFlush(response);
            }
        } else {
            logger.error("Invalid channel registration message received: {}", registrationMessage);
            ChannelRegistrationResponse response = new ChannelRegistrationResponse(false, "Invalid channel registration details.");
            ctx.writeAndFlush(response);
        }
    }

    private void fetchChannelDetailsFromServer(ChannelRegistrationMessage registrationMessage, ChannelHandlerContext ctx) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                     .channel(NioSocketChannel.class)
                     .option(ChannelOption.SO_KEEPALIVE, true)
                     .handler(new ChannelInitializer<Channel>() {
                         @Override
                         protected void initChannel(Channel ch) throws Exception {
                             ch.pipeline().addLast(new ChannelDetailsClientHandler(ctx, registrationMessage));
                         }
                     });

            // Connect to the channel server (host and port provided in registrationMessage)
            ChannelFuture future = bootstrap.connect(registrationMessage.getHost(), registrationMessage.getPort()).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("Error connecting to the channel server for: {}", registrationMessage.getChannelName(), e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private boolean isValidMessage(ChannelRegistrationMessage msg) {
        return msg.getChannelName() != null && !msg.getChannelName().isEmpty()
                && msg.getHost() != null && !msg.getHost().isEmpty()
                && msg.getPort() > 0;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in WorldServerHandler", cause);
        ctx.close();
    }
}
