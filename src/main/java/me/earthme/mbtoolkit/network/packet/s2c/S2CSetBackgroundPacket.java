package me.earthme.mbtoolkit.network.packet.s2c;

import me.earthme.mbtoolkit.ServerMain;
import me.earthme.mbtoolkit.network.handle.NettyClientHandler;
import me.earthme.mbtoolkit.network.packet.c2s.C2SProcessedBackgroundPacket;

public class S2CSetBackgroundPacket implements S2CPacket {
    private final byte[] backgroundPictureData;
    private final long id;

    public S2CSetBackgroundPacket(byte[] backgroundPictureData, long id) {
        this.backgroundPictureData = backgroundPictureData;
        this.id = id;
    }

    @Override
    public void process(NettyClientHandler handler) {
        if (this.id != -1){
            handler.setLastBGId(this.id);
        }
        ServerMain.getBackgroundForceThread().setBackground(this.backgroundPictureData);
        handler.getChannel().writeAndFlush(new C2SProcessedBackgroundPacket(handler.getLastBGId()));
    }
}
