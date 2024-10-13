package io.jyberion.mmorpg.common.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jyberion.mmorpg.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Object> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public MessageEncoder() {
        super();
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) {
        boolean accept = msg instanceof Message;
        System.out.println("MessageEncoder acceptOutboundMessage called with msg type: " + msg.getClass().getName() + ", accept: " + accept);
        System.out.println("Message interface classloader: " + Message.class.getClassLoader());
        System.out.println("msg classloader: " + msg.getClass().getClassLoader());
        return accept;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println("MessageEncoder encode called with msg type: " + msg.getClass().getName());
        if (msg instanceof Message) {
            System.out.println("Encoding message: " + msg.getClass().getSimpleName());
            byte[] data = objectMapper.writeValueAsBytes(msg);
            out.writeBytes(data);
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + msg.getClass());
        }
    }
}
