package me.earthme.mbtoolkit.network.packet.c2s;

import me.earthme.mbtoolkit.network.handle.NettyServerHandler;

import java.util.UUID;

public class C2SSetBackGroundPacket implements C2SPacket{
    private final UUID userUUID;
    private final byte[] data;

    public C2SSetBackGroundPacket(UUID userUUID, byte[] data) {
        this.userUUID = userUUID;
        this.data = data;
    }

    @Override
    public void process(NettyServerHandler handler) {
        if (handler.isPassedAuth()){
        }
    }
}
