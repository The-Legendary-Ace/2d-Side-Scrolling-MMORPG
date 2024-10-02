package io.jyberion.mmorpg.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerHandler extends SimpleChannelInboundHandler<Object> { // Handle both API requests and ChatMessages

    private static final ConcurrentHashMap<String, ChatChannel> channels = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            handleApiCommand(ctx, (String) msg); // Handle incoming API commands (e.g., ANNOUNCE)
        } else if (msg instanceof ChatMessage) {
            handleChatMessage(ctx, (ChatMessage) msg); // Handle normal chat messages
        } else {
            ctx.writeAndFlush("Unknown message type!");
        }
    }

    // Method to handle API commands from the API server (e.g., ANNOUNCE)
    private void handleApiCommand(ChannelHandlerContext ctx, String command) {
        if (command.startsWith("ANNOUNCE:")) {
            String announcement = command.substring("ANNOUNCE:".length()).trim();
            sendGlobalAnnouncement(announcement);
            ctx.writeAndFlush("Announcement sent: " + announcement);
        } else {
            ctx.writeAndFlush("Unknown command: " + command);
        }
    }

    // Method to handle normal chat messages (from players)
    private void handleChatMessage(ChannelHandlerContext ctx, ChatMessage msg) {
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
                ctx.writeAndFlush(new ChatMessage("System", "Unknown chat channel!"));
        }
    }

    // Method to handle global chat messages
    private void handleGlobalChat(ChannelHandlerContext ctx, ChatMessage msg) {
        // Send the message to all connected clients in the global channel
        ChatChannel globalChannel = channels.get("GLOBAL");
        if (globalChannel != null) {
            globalChannel.broadcastMessage(msg);
            logChatMessage(msg, "Global");
        } else {
            ctx.writeAndFlush(new ChatMessage("System", "Global chat is currently unavailable."));
        }
    }

    // Method to broadcast announcements to the global channel (for API or system-wide announcements)
    public void sendGlobalAnnouncement(String announcement) {
        ChatMessage announcementMsg = new ChatMessage("Server", announcement);
        announcementMsg.setChannelType("GLOBAL");

        ChatChannel globalChannel = channels.get("GLOBAL");
        if (globalChannel != null) {
            globalChannel.broadcastMessage(announcementMsg);
            logChatMessage(announcementMsg, "Global"); // Log the announcement
        } else {
            System.err.println("Global chat channel is not available for announcements.");
        }
    }

    // Method to handle guild chat messages
    private void handleGuildChat(ChannelHandlerContext ctx, ChatMessage msg) {
        ChatChannel guildChannel = channels.get(msg.getGuildId());
        if (guildChannel != null) {
            guildChannel.broadcastMessage(msg);
            logChatMessage(msg, "Guild");
        } else {
            ctx.writeAndFlush(new ChatMessage("System", "Guild chat is unavailable."));
        }
    }

    // Method to handle private chat messages between players
    private void handlePrivateChat(ChannelHandlerContext ctx, ChatMessage msg) {
        ChatChannel privateChannel = channels.get(msg.getRecipient());
        if (privateChannel != null) {
            privateChannel.sendMessage(msg);
            logChatMessage(msg, "Private Message");
        } else {
            ctx.writeAndFlush(new ChatMessage("System", "User not found for private message."));
        }
    }

    // Method to handle local chat messages (for geographically close players)
    private void handleLocalChat(ChannelHandlerContext ctx, ChatMessage msg) {
        // Log the local message to the log directory
        logChatMessage(msg, "Local");
    }

    // Method for logging chat messages
    private void logChatMessage(ChatMessage msg, String messageType) {
        String logMessage = String.format("[%s] %s: %s", messageType, msg.getSender(), msg.getContent());
        ChatLogger.logMessage(msg.getSender(), messageType, logMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
