package me.earthme.mbtoolkit.network.packet.c2s;

import me.earthme.mbtoolkit.network.handle.NettyServerHandler;

public class C2SProcessedBackgroundPacket implements C2SPacket{
    private final long id;

    public C2SProcessedBackgroundPacket(long id) {
        this.id = id;
    }

    @Override
    public void process(NettyServerHandler handler) {
        handler.processProcessedBackgroundPacket(this);
    }

    public long getId() {
        return this.id;
    }
}
