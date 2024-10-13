package io.jyberion.mmorpg.channel;

import io.jyberion.mmorpg.common.message.ChannelInfoMessage;
import io.jyberion.mmorpg.common.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChannelDetailsClientHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        // Handle the incoming request for channel details from the World Server
        if (msg instanceof ChannelDetailsRequest) {
            // Send back the channel details as a response
            ChannelInfoMessage channelInfoMessage = new ChannelInfoMessage(
                // Populate with actual channel info
            );
            ctx.writeAndFlush(channelInfoMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
