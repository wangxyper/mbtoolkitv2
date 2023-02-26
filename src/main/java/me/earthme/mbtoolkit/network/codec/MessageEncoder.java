package me.earthme.mbtoolkit.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.earthme.mbtoolkit.network.packet.Message;

public class MessageEncoder extends MessageToByteEncoder<Message<?>>{
    @Override
    protected void encode(ChannelHandlerContext ctx, Message<?> msg, ByteBuf out) {
        out.writeByte(msg.getHead());
        msg.writeMessageData(out);
    }
}
