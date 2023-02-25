package me.earthme.mbtoolkit.network.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.earthme.mbtoolkit.network.packet.c2s.C2SPacket;
import me.earthme.mbtoolkit.network.packet.s2c.S2CPacket;

public class NettyServerHandler extends SimpleChannelInboundHandler<C2SPacket>{
    private static final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private Channel channel;
    private volatile boolean passedAuth;

    public boolean isPassedAuth() {
        return this.passedAuth;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        this.channel = ctx.channel();
    }

    public void send(S2CPacket packet){
        this.channel.writeAndFlush(packet);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public static ChannelGroup getConnectedClients() {
        return connectedClients;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, C2SPacket msg) throws Exception {

    }
}
