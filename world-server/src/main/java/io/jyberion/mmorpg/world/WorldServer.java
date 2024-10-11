package io.jyberion.mmorpg.world;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jyberion.mmorpg.world.handler.WorldServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldServer {

    public static void main(String[] args) throws Exception {
        // Load configuration file from classpath
        try (InputStream input = WorldServer.class.getClassLoader().getResourceAsStream("world.properties")) {
            if (input == null) {
                throw new RuntimeException("world.properties not found in classpath");
            }
            Properties properties = new Properties();
            properties.load(input);
            properties.forEach((key, value) -> ConfigLoader.loadProperty((String) key, (String) value));
        }

        String worldName = ConfigLoader.getProperty("world.name");
        int port = Integer.parseInt(ConfigLoader.getProperty("world.port"));

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WorldServerInitializer(worldName));

            ChannelFuture f = b.bind(port).sync();
            System.out.println("World Server " + worldName + " is running on port " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}