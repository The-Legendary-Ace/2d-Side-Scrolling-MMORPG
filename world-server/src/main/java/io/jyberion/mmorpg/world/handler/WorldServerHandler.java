package io.jyberion.mmorpg.world.handler;

import io.jyberion.mmorpg.common.message.PlayerActionMessage;
import io.jyberion.mmorpg.world.model.GameWorld;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldServerHandler extends SimpleChannelInboundHandler<PlayerActionMessage> {
    private static final Logger logger = LoggerFactory.getLogger(WorldServerHandler.class);
    private final GameWorld gameWorld = new GameWorld();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String token = ctx.channel().attr(AttributeKey.<String>valueOf("token")).get();
        gameWorld.addPlayer(token).thenRun(() -> {
            logger.info("Player added to the game world");
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PlayerActionMessage msg) throws Exception {
        String token = ctx.channel().attr(AttributeKey.<String>valueOf("token")).get();
        gameWorld.processPlayerAction(token, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String token = ctx.channel().attr(AttributeKey.<String>valueOf("token")).get();
        gameWorld.removePlayer(token).thenRun(() -> {
            logger.info("Player removed from the game world");
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in WorldServerHandler", cause);
        ctx.close();
    }
}
