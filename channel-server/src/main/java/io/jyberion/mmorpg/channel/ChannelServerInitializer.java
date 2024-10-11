package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.channel.handler.ChannelServerHandler;
import io.jyberion.mmorpg.common.network.MessageDecoder;
import io.jyberion.mmorpg.common.network.MessageEncoder;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.TooLongFrameException;

public class ChannelServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ChannelInfo channelInfo;

    public ChannelServerInitializer(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Add LengthFieldBasedFrameDecoder to handle large frames
        pipeline.addLast(new LengthFieldBasedFrameDecoder(10 * 1024 * 1024, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        
        // Add encoders and decoders
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());

        // Add your handler
        pipeline.addLast(new ChannelServerHandler(channelInfo));

        // Print current pipeline for debugging
        System.out.println("Current pipeline handlers: " + pipeline.names());

        // Add a custom handler for exceptions
        pipeline.addLast("exceptionHandler", new SimpleChannelInboundHandler<Object>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                // Just pass through
                ctx.fireChannelRead(msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                if (cause instanceof TooLongFrameException) {
                    System.err.println("TooLongFrameException: Frame too large, discarding message.");
                } else {
                    cause.printStackTrace();
                }
                ctx.close();
            }
        });
    }
}