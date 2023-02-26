package me.earthme.mbtoolkit.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.earthme.mbtoolkit.Main;
import me.earthme.mbtoolkit.network.handler.NettyClientHandler;
import me.earthme.mbtoolkit.network.packet.Message;

public class ServerSyncWallpaperMessage implements Message<NettyClientHandler> {
    private byte[] data;

    public ServerSyncWallpaperMessage(){}

    public ServerSyncWallpaperMessage(byte[] data){
        this.data = data;
    }

    @Override
    public byte getHead() {
        return 0x04;
    }

    @Override
    public void writeMessageData(ByteBuf byteBuf) {
        byteBuf.writeBytes(this.data);
    }

    @Override
    public void readMessageData(ByteBuf byteBuf) {
        byteBuf.readBytes((this.data = new byte[byteBuf.readableBytes()]));
    }

    @Override
    public void process(NettyClientHandler processor) {
        Main.getBackgroundForceThread().setBackground(this.data);
    }
}
