package me.earthme.mbtoolkit.network.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.earthme.mbtoolkit.network.packet.s2c.S2CPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class NettyClientHandler extends SimpleChannelInboundHandler<S2CPacket> {
    public static final Logger logger = LogManager.getLogger();
    private volatile long lastBGId = 0;
    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacket msg) {
        msg.process(this);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public long getLastBGId() {
        return this.lastBGId;
    }

    public void setLastBGId(long lastBGId) {
        this.lastBGId = lastBGId;
    }
}
