package me.earthme.mbtoolkit.network.packet.client;

import io.netty.buffer.ByteBuf;
import me.earthme.mbtoolkit.network.handle.NettyServerHandler;
import me.earthme.mbtoolkit.network.packet.Message;

public class ClientBackgroundChangedPacket implements Message<NettyServerHandler> {
    private int id;

    public ClientBackgroundChangedPacket(){}

    public ClientBackgroundChangedPacket(int id) {
        this.id = id;
    }

    @Override
    public byte getHead() {
        return 0x02;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void writeMessageData(ByteBuf byteBuf) {
        byteBuf.writeInt(this.id);
    }

    @Override
    public void readMessageData(ByteBuf byteBuf) {
        this.id = byteBuf.readInt();
    }

    @Override
    public void process(NettyServerHandler serverHandler) {
        serverHandler.checkAndResetBKId(this.id);
    }
}
