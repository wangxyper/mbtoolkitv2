package me.earthme.mbtoolkit.network.packet.client;

import io.netty.buffer.ByteBuf;
import me.earthme.mbtoolkit.network.handler.NettyServerHandler;
import me.earthme.mbtoolkit.network.packet.Message;
import java.nio.charset.StandardCharsets;

public class ClientGetAndSetBackgroundMessage implements Message<NettyServerHandler> {
    private String clientMd5;

    public ClientGetAndSetBackgroundMessage(){}

    public ClientGetAndSetBackgroundMessage(String md5){
        this.clientMd5 = md5;
    }

    @Override
    public byte getHead() {
        return 0x05;
    }

    @Override
    public void writeMessageData(ByteBuf byteBuf) {
        byteBuf.writeBytes(this.clientMd5.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void readMessageData(ByteBuf byteBuf) {
        final byte[] md5ByteArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(md5ByteArray);
        this.clientMd5 = new String(md5ByteArray,StandardCharsets.UTF_8);
    }

    @Override
    public void process(NettyServerHandler processor) {
        if (processor.getServerInstance().getCurrentWallpaperMd5().equals(this.clientMd5)){
            return;
        }
        processor.getServerInstance().getWallPaperData().thenAcceptAsync(processor::switchBackgroundPicture);
    }
}
