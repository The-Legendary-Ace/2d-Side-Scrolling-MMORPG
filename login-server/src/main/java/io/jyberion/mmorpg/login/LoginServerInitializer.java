// LoginServerInitializer.java
package io.jyberion.mmorpg.login;

import io.jyberion.mmorpg.login.handler.LoginServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import org.hibernate.SessionFactory;

public class LoginServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SessionFactory sessionFactory;

    public LoginServerInitializer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add frame decoder, prepender, and custom encoders/decoders
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());

        // Pass sessionFactory to LoginServerHandler constructor
        pipeline.addLast(new LoginServerHandler(sessionFactory));
    }
}
