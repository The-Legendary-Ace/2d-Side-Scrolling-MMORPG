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
        byte[] data = objectMapper.writeValueAsBytes(msg);
        out.writeInt(data.length);  // Write the length of the serialized message
        out.writeBytes(data);       // Write the actual message data
    }
}
