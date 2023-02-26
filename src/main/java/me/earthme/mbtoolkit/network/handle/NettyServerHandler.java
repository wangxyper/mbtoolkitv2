package me.earthme.mbtoolkit.network.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.earthme.mbtoolkit.network.packet.c2s.C2SPacket;
import me.earthme.mbtoolkit.network.packet.c2s.C2SProcessedBackgroundPacket;
import me.earthme.mbtoolkit.network.packet.s2c.S2CPacket;
import me.earthme.mbtoolkit.network.packet.s2c.S2CSetBackgroundPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class NettyServerHandler extends SimpleChannelInboundHandler<C2SPacket>{
    private static final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ExecutorService loopWorker = Executors.newCachedThreadPool();
    private Channel channel;
    public static final List<NettyServerHandler> handlers = new CopyOnWriteArrayList<>();

    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
    private S2CSetBackgroundPacket lastProcessingBackgroundPacket = null;
    private long lastProcessingBackgroundPacketId = 0;
    private byte[] lastBackgroundData = null;

    public NettyServerHandler(){
        handlers.add(this);
    }

    public static ChannelGroup getConnectedClients() {
        return connectedClients;
    }

    public void processProcessedBackgroundPacket(@NotNull C2SProcessedBackgroundPacket packet){
        if (this.lastProcessingBackgroundPacketId == packet.getId()){
            this.lastBackgroundData = null;
            this.lastProcessingBackgroundPacketId = 0;
            this.lastProcessingBackgroundPacket = null;
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(loopWorker::shutdownNow));
    }

    public void setBackground(byte[] data){
        this.taskQueue.offer(()->{
            this.lastBackgroundData = data;
            this.lastProcessingBackgroundPacketId = System.nanoTime();
            this.lastProcessingBackgroundPacket = new S2CSetBackgroundPacket(this.lastBackgroundData,this.lastProcessingBackgroundPacketId);
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        this.channel = ctx.channel();
        this.tick();
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        handlers.remove(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, C2SPacket msg) {
        msg.process(this);
    }

    public void tick(){
        if (this.channel.isOpen()){
            loopWorker.execute(this::tick);
        }
        final long time = System.nanoTime();
        try {
            this.doTickInternal();

            Runnable task;
            while ((task = this.taskQueue.poll())!=null){
                task.run();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            final long usedTime = System.nanoTime() - time;
            if (usedTime < 50_000_000L){
                LockSupport.parkNanos(50_000_000L - usedTime);
            }
        }
    }

    private long tickCounter;

    private void doTickInternal(){
        this.tickCounter++;

        if (this.tickCounter % 40 == 0 && this.lastProcessingBackgroundPacket!=null && this.lastProcessingBackgroundPacketId != 0 && this.lastBackgroundData!=null){
            this.channel.writeAndFlush(new S2CSetBackgroundPacket(this.lastBackgroundData,-1));
        }
    }
}
