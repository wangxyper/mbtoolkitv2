package me.earthme.mbtoolkit.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.earthme.mbtoolkit.ServerMain;
import me.earthme.mbtoolkit.network.handle.NettyClientHandler;
import me.earthme.mbtoolkit.network.packet.Message;
import me.earthme.mbtoolkit.network.packet.client.ClientBackgroundChangedPacket;

public class ServerChangeBackgroundCommandMessage implements Message<NettyClientHandler> {
    private byte[] data;
    private int id;

    public ServerChangeBackgroundCommandMessage(){}

    public ServerChangeBackgroundCommandMessage(byte[] data,int id) {
        this.data = data;
        this.id = id;
    }

    @Override
    public byte getHead() {
        return 0x01;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void writeMessageData(ByteBuf byteBuf) {
        byteBuf.writeInt(this.id);
        byteBuf.writeBytes(this.data);
    }

    @Override
    public void readMessageData(ByteBuf byteBuf) {
        this.id = byteBuf.readInt();
        byteBuf.readBytes((this.data = new byte[byteBuf.readableBytes()]));
    }

    @Override
    public void process(NettyClientHandler clientHandler) {
        ServerMain.getBackgroundForceThread().setBackground(this.data);
        clientHandler.send(new ClientBackgroundChangedPacket(this.id));
    }
}
