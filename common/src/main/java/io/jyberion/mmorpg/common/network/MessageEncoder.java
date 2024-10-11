package io.jyberion.mmorpg.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jyberion.mmorpg.common.message.Message;

public class MessageEncoder extends MessageToByteEncoder<Message> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        try {
            // Check if the message is null
            if (msg == null) {
                System.err.println("Message is null. Skipping encoding.");
                return;
            }

            // Show the message before encoding
            System.out.println("Encoding message: " + msg);

            // Serialize the message to JSON
            byte[] data = objectMapper.writeValueAsBytes(msg);
            int length = data.length;

            // Write the length of the data and then the data itself
            out.writeInt(length);
            out.writeBytes(data);

            System.out.println("Encoded message with length: " + length);
        } catch (Exception e) {
            System.err.println("Error during encoding: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
