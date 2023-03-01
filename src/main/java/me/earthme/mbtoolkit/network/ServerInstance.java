package me.earthme.mbtoolkit.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import me.earthme.mbtoolkit.network.codec.MessageDecoder;
import me.earthme.mbtoolkit.network.codec.MessageEncoder;
import me.earthme.mbtoolkit.network.handler.NettyServerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class ServerInstance {
    private static final Logger logger = LogManager.getLogger();
    private final NioEventLoopGroup currentLoopGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
    private ChannelFuture channelFuture;

    private final File currentWallpaper = new File("wp.mbcache");
    private byte[] currentWallpaperCache = null;
    private final Object cacheLock = new Object();

    public void start(@NotNull InetSocketAddress address){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(this.currentLoopGroup,this.eventExecutors)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(@NotNull Channel ch) {
                        logger.info("Connection incoming:{}",ch);
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(2077721600,0,4,0,4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new MessageDecoder())
                                .addLast(new MessageEncoder())
                                .addLast(new NettyServerHandler());
                    }
                });
        try {
            this.channelFuture = serverBootstrap.bind(address).sync();
        } catch (InterruptedException e) {
            logger.error(e);
        }
        logger.info("Server bind on : {}:{}",address.getHostName(),address.getPort());
    }

    public void shutdown(){
        logger.info("Shutting down server");
        this.channelFuture.channel().close();
        this.currentLoopGroup.shutdownGracefully();
        this.eventExecutors.shutdownGracefully();
    }

    public void setCurrentWallpaper(byte[] data){
        try {
            synchronized (this.cacheLock){
                this.currentWallpaperCache = data;
            }

            Files.write(this.currentWallpaper.toPath(),data);

            for (NettyServerHandler handler : NettyServerHandler.handlers){
                handler.switchBackgroundPicture(data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public CompletableFuture<byte[]> getWallPaperData(){
        return CompletableFuture.supplyAsync(()->{
            if (!this.currentWallpaper.exists()){
                return null;
            }
            synchronized (this.cacheLock){
                if (this.currentWallpaperCache != null){
                    return this.currentWallpaperCache;
                }
                try {
                    return (this.currentWallpaperCache = Files.readAllBytes(this.currentWallpaper.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }
}
