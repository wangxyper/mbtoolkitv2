package me.earthme.mbtoolkit.network.packet.s2c;

import me.earthme.mbtoolkit.network.handle.NettyClientHandler;
import me.earthme.mbtoolkit.network.handle.NettyServerHandler;

public class S2CSetBackgroundPacket implements S2CPacket {
    private final byte[] backgroundPictureData;

    public S2CSetBackgroundPacket(byte[] backgroundPictureData) {
        this.backgroundPictureData = backgroundPictureData;
    }

    @Override
    public void process(NettyClientHandler handler) {

    }
}
