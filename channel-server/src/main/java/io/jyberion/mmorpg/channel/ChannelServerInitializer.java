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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServerInitializer.class);
    private final ChannelInfo channelInfo;

    public ChannelServerInitializer(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        logger.info("Initializing ChannelServer pipeline for channel: {}", channelInfo.getChannelName());

        // Add LengthFieldBasedFrameDecoder to handle large frames
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4)); // Max length: 1MB, length offset: 0, length field size: 4 bytes.
        pipeline.addLast(new LengthFieldPrepender(4)); // Prepends length to outgoing messages for framing.

        // Add custom encoders and decoders
        pipeline.addLast(new MessageDecoder()); // Decodes incoming byte streams into objects.
        pipeline.addLast(new MessageEncoder()); // Encodes outgoing objects into byte streams.

        // Add your main handler for channel operations
        pipeline.addLast(new ChannelServerHandler(channelInfo)); // Handles the core business logic.

        // Print current pipeline for debugging purposes
        logger.debug("Current pipeline handlers: {}", pipeline.names());

        // Add a custom handler for exception handling
        pipeline.addLast("exceptionHandler", new SimpleChannelInboundHandler<Object>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                // Pass through the message to the next handler
                ctx.fireChannelRead(msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                if (cause instanceof TooLongFrameException) {
                    logger.error("TooLongFrameException: Frame too large, discarding message.");
                } else {
                    logger.error("Exception caught in pipeline", cause);
                }
                ctx.close(); // Close the connection in case of an exception
            }
        });
    }
}
