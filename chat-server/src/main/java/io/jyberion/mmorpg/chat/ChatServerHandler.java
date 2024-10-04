package io.jyberion.mmorpg.chat;

import io.jyberion.mmorpg.common.entity.ChatLog;
import io.jyberion.mmorpg.common.message.ChatMessage;
import io.jyberion.mmorpg.common.security.TokenUtil;
import io.jyberion.mmorpg.common.service.ChatLogService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private final ChatChannelManager chatChannelManager = ChatChannelManager.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
        String token = msg.getToken();
        String username = TokenUtil.validateToken(token);
        if (username == null) {
            logger.warn("Invalid token received");
            ctx.close();
            return;
        }

        msg.setSender(username);

        switch (msg.getChannelType()) {
            case "GLOBAL":
                handleGlobalChat(ctx, msg);
                break;
            case "GUILD":
                handleGuildChat(ctx, msg);
                break;
            case "PRIVATE":
                handlePrivateChat(ctx, msg);
                break;
            case "LOCAL":
                handleLocalChat(ctx, msg);
                break;
            default:
                ctx.writeAndFlush(new ChatMessage("System", "ERROR", "Unknown chat channel!"));
        }
    }

    private void handleGlobalChat(ChannelHandlerContext ctx, ChatMessage msg) {
        // Broadcast message to all clients
        chatChannelManager.broadcastToGlobal(msg);
        // Log the message
        ChatLogService.saveChatLogAsync(new ChatLog(msg.getSender(), "GLOBAL", msg.getContent()));
    }

    private void handleGuildChat(ChannelHandlerContext ctx, ChatMessage msg) {
        // Implement guild chat handling
        chatChannelManager.broadcastToGuild(msg.getSender(), msg);
        // Log the message
        ChatLogService.saveChatLogAsync(new ChatLog(msg.getSender(), "GUILD", msg.getContent()));
    }

    private void handlePrivateChat(ChannelHandlerContext ctx, ChatMessage msg) {
        // Implement private chat handling
        chatChannelManager.sendPrivateMessage(msg.getSender(), msg.getRecipient(), msg);
        // Log the message
        ChatLogService.saveChatLogAsync(new ChatLog(msg.getSender(), "PRIVATE", msg.getContent()));
    }

    private void handleLocalChat(ChannelHandlerContext ctx, ChatMessage msg) {
        // Implement local chat handling
        chatChannelManager.broadcastToLocal(msg.getSender(), msg);
        // Log the message
        ChatLogService.saveChatLogAsync(new ChatLog(msg.getSender(), "LOCAL", msg.getContent()));
    }

    // Method to send global announcements
    public void sendGlobalAnnouncement(String announcement) {
        ChatMessage msg = new ChatMessage("System", "GLOBAL", announcement);
        chatChannelManager.broadcastToGlobal(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in ChatServerHandler", cause);
        ctx.close();
    }
}
