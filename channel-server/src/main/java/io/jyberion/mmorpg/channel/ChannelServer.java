package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelServer {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServer.class);
    private final int port;
    private final ChannelInfo channelInfo;
    private final String worldServerHost;
    private final int worldServerPort;
    private final String worldId; // Add worldId

    public ChannelServer(int port, ChannelInfo channelInfo, String worldServerHost, int worldServerPort, String worldId) {
        this.port = port;
        this.channelInfo = channelInfo;
        this.worldServerHost = worldServerHost;
        this.worldServerPort = worldServerPort;
        this.worldId = worldId; // Initialize worldId
    }

    public void start() throws InterruptedException {
        // Start the Channel Server
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                           .channel(NioServerSocketChannel.class)
                           .childHandler(new ChannelServerInitializer(channelInfo))
                           .option(ChannelOption.SO_BACKLOG, 128)
                           .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = serverBootstrap.bind(port).sync();
            logger.info("Channel Server started on port {}", port);

            // After starting the server, connect to the World Server
            connectToWorldServer();

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("Channel Server shut down");
        }
    }

    private void connectToWorldServer() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.SO_KEEPALIVE, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                     pipeline.addLast(new LengthFieldPrepender(4));
                     pipeline.addLast(new MessageDecoder());
                     pipeline.addLast(new MessageEncoder());
                     pipeline.addLast(new ChannelRegistrationHandler(channelInfo, worldId)); // Pass worldId here
                 }
             });

            logger.info("Connecting to World Server at {}:{}", worldServerHost, worldServerPort);
            ChannelFuture f = b.connect(worldServerHost, worldServerPort).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        // Load configuration file from classpath
        try (InputStream input = ChannelServer.class.getClassLoader().getResourceAsStream("channel.properties")) {
            if (input == null) {
                throw new RuntimeException("channel.properties not found in classpath");
            }
            Properties properties = new Properties();
            properties.load(input);
            properties.forEach((key, value) -> ConfigLoader.loadProperty((String) key, (String) value));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            String channelName = ConfigLoader.get("channel.name");
            String channelAddress = ConfigLoader.get("channel.address");
            String channelPortStr = ConfigLoader.get("channel.port");
            String channelMaxPlayersStr = ConfigLoader.get("channel.maxPlayers");
            String worldServerHost = ConfigLoader.get("world.server.host");
            String worldServerPortStr = ConfigLoader.get("world.server.port");
            String worldId = ConfigLoader.get("world.id"); // Add worldId

            // Validate and parse the configuration values
            if (channelName == null || channelAddress == null || channelPortStr == null || channelMaxPlayersStr == null || worldServerHost == null || worldServerPortStr == null || worldId == null) {
                throw new IllegalArgumentException("Missing required configuration properties");
            }

            int channelPort = Integer.parseInt(channelPortStr);
            int channelMaxPlayers = Integer.parseInt(channelMaxPlayersStr);
            int worldServerPort = Integer.parseInt(worldServerPortStr);

            ChannelInfo channelInfo = new ChannelInfo(
                    worldId, // Pass worldId here
                    channelName,
                    channelAddress,
                    channelPort,
                    0,
                    channelMaxPlayers,
                    1 // Assuming 1 represents 'online' status for the channel
            );

            // Start the Channel Server
            new ChannelServer(channelPort, channelInfo, worldServerHost, worldServerPort, worldId).start(); // Pass worldId

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            System.err.println("Configuration error: " + e.getMessage());
            System.exit(1);
        }
    }
}
