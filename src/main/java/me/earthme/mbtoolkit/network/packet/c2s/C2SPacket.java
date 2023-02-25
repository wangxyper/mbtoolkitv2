package me.earthme.mbtoolkit.network.packet.c2s;

import me.earthme.mbtoolkit.network.handle.NettyClientHandler;
import me.earthme.mbtoolkit.network.handle.NettyServerHandler;

import java.io.Serializable;

public interface C2SPacket extends Serializable {
    void process(NettyServerHandler handler);
}
