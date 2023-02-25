package me.earthme.mbtoolkit.network.packet.s2c;

import me.earthme.mbtoolkit.network.handle.NettyClientHandler;
import me.earthme.mbtoolkit.network.handle.NettyServerHandler;

import java.io.Serializable;

public interface S2CPacket extends Serializable {
    void process(NettyClientHandler handler);
}
