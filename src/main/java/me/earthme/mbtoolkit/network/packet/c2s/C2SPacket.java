package me.earthme.mbtoolkit.network.packet.c2s;

import me.earthme.mbtoolkit.network.handle.NettyServerHandler;

public interface C2SPacket {
    void process(NettyServerHandler handler);
}
