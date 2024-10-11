package io.jyberion.mmorpg.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jyberion.mmorpg.common.message.Message;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            // Print the number of readable bytes before attempting to read
            System.out.println("decode method called with readable bytes: " + in.readableBytes());
            
            // Minimum size for an encoded message to contain the length field
            if (in.readableBytes() < 4) {
                System.out.println("Not enough bytes to read length field. Readable bytes: " + in.readableBytes());
                return;
            }

            in.markReaderIndex();  // Mark the current reader index for later reset if necessary

            // Read the length field to know the full message length
            int length = in.readInt();
            System.out.println("Frame length read: " + length);
            System.out.println("Readable bytes after reading length: " + in.readableBytes());

            if (length > 10 * 1024 * 1024) { // Check if the length exceeds the maximum frame size
                System.err.println("Frame length exceeds maximum allowed size: " + length);
                in.resetReaderIndex();
                throw new TooLongFrameException("Frame length exceeds maximum allowed size: " + length);
            }

            if (in.readableBytes() < length) {
                System.out.println("Not enough bytes available to read full frame. Expected: " + length + ", Available: " + in.readableBytes());
                in.resetReaderIndex();  // Reset if we don't have enough data yet
                return;
            }

            byte[] data = new byte[length];
            in.readBytes(data);

            // Deserialize the JSON data into a Message object
            Message msg = objectMapper.readValue(data, Message.class);
            out.add(msg);  // Add the decoded message to the output list
            System.out.println("Successfully decoded message: " + msg);
        } catch (Exception e) {
            System.err.println("Error during decoding: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof TooLongFrameException) {
            System.err.println("TooLongFrameException: Discarded a message that exceeded allowed frame length.");
        } else {
            System.err.println("Exception caught: " + cause.getMessage());
            cause.printStackTrace();
        }
        ctx.close();
    }
}
