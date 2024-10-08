package io.jyberion.mmorpg.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jyberion.mmorpg.common.message.Message;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Minimum size for an encoded message to contain the length field
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();  // Mark the current reader index for later reset if necessary

        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();  // Reset if we don't have enough data yet
            return;
        }

        byte[] data = new byte[length];
        in.readBytes(data);

        // Deserialize the JSON data into a Message object
        Message msg = objectMapper.readValue(data, Message.class);
        out.add(msg);  // Add the decoded message to the output list
    }
}
