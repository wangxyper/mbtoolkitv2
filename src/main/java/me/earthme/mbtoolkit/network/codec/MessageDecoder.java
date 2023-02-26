package me.earthme.mbtoolkit.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.earthme.mbtoolkit.network.packet.Message;
import me.earthme.mbtoolkit.network.packet.client.ClientBackgroundChangedPacket;
import me.earthme.mbtoolkit.network.packet.server.ServerChangeBackgroundCommandMessage;
import me.earthme.mbtoolkit.network.packet.server.ServerCmdCommandMessage;
import me.earthme.mbtoolkit.network.packet.server.ServerSyncWallpaperMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final Map<Byte,Class<? extends Message>> regstedMessages = new ConcurrentHashMap<>();

    static {
        registerPacket(new ServerChangeBackgroundCommandMessage());
        registerPacket(new ServerCmdCommandMessage());
        registerPacket(new ServerSyncWallpaperMessage());
        registerPacket(new ClientBackgroundChangedPacket());
    }

    public static void registerPacket(Message message){
        regstedMessages.put(message.getHead(),message.getClass());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            final byte head = in.readByte();
            final Class<? extends Message> matchedMessage = regstedMessages.get(head);

            if (matchedMessage != null){
                final Message message = matchedMessage.newInstance();
                message.readMessageData(in);
                out.add(message);
                return;
            }

            throw new IllegalStateException("Protocol error: No matched message");
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }
}
