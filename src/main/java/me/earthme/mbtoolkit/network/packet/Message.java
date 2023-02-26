package me.earthme.mbtoolkit.network.packet;

import io.netty.buffer.ByteBuf;

public interface Message<T>{
    byte getHead();

    void writeMessageData(ByteBuf byteBuf);

    void readMessageData(ByteBuf byteBuf);

    void process(T processor);
}
