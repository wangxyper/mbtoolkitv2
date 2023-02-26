package me.earthme.mbtoolkit.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import me.earthme.mbtoolkit.network.handle.NettyClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.LockSupport;

public class NettySocketClient {
    private static final Logger logger = LogManager.getLogger();
    private final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private ChannelFuture future;
    private InetSocketAddress lastAddress;
    private boolean flag = false;

    public void connect(InetSocketAddress socketAddress) throws InterruptedException {
        this.lastAddress = socketAddress;
        this.bootstrap.group(this.loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(@NotNull Channel ch) {
                        ch.pipeline()
                                .addLast(new ObjectEncoder())
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast(new NettyClientHandler());
                    }
                });
        this.future = this.bootstrap.connect(socketAddress).sync();
        logger.info("Connected to server");
        if (!flag){
            final Thread reconnector = new Thread(()->{
                while (true){
                    try {
                        this.blockUntilDisconnected();
                        logger.info("Connection lost!Reconnecting to server");
                        this.connect(this.lastAddress);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            reconnector.setPriority(3);
            reconnector.setDaemon(true);
            reconnector.start();
            flag = true;
        }
    }

    public void blockUntilDisconnected(){
        while (this.future.channel().isOpen()){
            LockSupport.parkNanos(1000_000_000);
        }
    }
}
