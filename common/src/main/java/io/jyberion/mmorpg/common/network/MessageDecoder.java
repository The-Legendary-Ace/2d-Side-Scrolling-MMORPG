package io.jyberion.mmorpg.common.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jyberion.mmorpg.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public MessageDecoder() {
        super();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            // Check if there are bytes to read
            if (in.readableBytes() > 0) {
                // Read all available bytes
                byte[] data = new byte[in.readableBytes()];
                in.readBytes(data);

                // Deserialize the JSON data into a Message object
                Message msg = objectMapper.readValue(data, Message.class);

                // Add the decoded message to the output list
                out.add(msg);

                System.out.println("Successfully decoded message: " + msg);
            }
        } catch (Exception e) {
            System.err.println("Error during decoding: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Exception in MessageDecoder: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
