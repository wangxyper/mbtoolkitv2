package me.earthme.mbtoolkit.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

public class NetworkSocketServer {
    private final NioEventLoopGroup currentLoopGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap;

    public void start(InetSocketAddress address){
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(this.currentLoopGroup,this.eventExecutors)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new ObjectEncoder())
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    }
                });
    }
}
