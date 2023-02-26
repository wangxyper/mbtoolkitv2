package me.earthme.mbtoolkit.network.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.earthme.mbtoolkit.network.packet.Message;
import me.earthme.mbtoolkit.network.packet.server.ServerChangeBackgroundCommandMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class NettyServerHandler extends SimpleChannelInboundHandler<Message<NettyServerHandler>>{

    private static final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ExecutorService loopWorker = Executors.newCachedThreadPool();
    public static final List<NettyServerHandler> handlers = new CopyOnWriteArrayList<>();

    private final Random random = new Random();
    private int lastBgId = -1;
    private boolean lastBgProcessed = false;
    private byte[] lastBgData;

    private long tickCounter;
    private Channel channel;
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final Queue<Message<NettyServerHandler>> messages = new ConcurrentLinkedQueue<>();

    public NettyServerHandler(){
        handlers.add(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<NettyServerHandler> msg) {
        this.messages.offer(msg);
    }


    public static ChannelGroup getConnectedClients() {
        return connectedClients;
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(loopWorker::shutdownNow));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        this.channel = ctx.channel();
        this.tick();
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        handlers.remove(this);
    }

    public void send(Message<NettyClientHandler> message){
        if (this.channel.eventLoop().inEventLoop()){
            this.channel.writeAndFlush(message);
        }else {
            this.channel.eventLoop().execute(()-> channel.writeAndFlush(message));
        }
    }

    public void tick(){
        final long time = System.nanoTime();
        try {
            this.doTickInternal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            final long usedTime = System.nanoTime() - time;
            if (usedTime < 50_000_000L){
                LockSupport.parkNanos(50_000_000L - usedTime);
            }
        }
        if (this.channel.isOpen()){
            loopWorker.execute(this::tick);
        }
    }

    private void doTickInternal() {
        this.tickCounter++;
        this.processMainThreadTasks();
        this.processPackets();

        if (this.tickCounter % 20 == 0 && this.lastBgId != -1 && !this.lastBgProcessed) {
            this.send(new ServerChangeBackgroundCommandMessage(this.lastBgData, this.lastBgId));
        }
    }

    protected void processMainThreadTasks(){
        Runnable task;
        while ((task = this.tasks.poll())!=null){
            try {
                task.run();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void switchBackgroundPicture(byte[] bytes){
        this.submitMainThreadTask(()->{
            this.lastBgId = this.random.nextInt();
            this.lastBgData = bytes;
            this.send(new ServerChangeBackgroundCommandMessage(this.lastBgData, this.lastBgId));
            this.lastBgProcessed = false;
        });
    }

    public void submitMainThreadTask(Runnable task){
        this.tasks.offer(task);
    }

    private void processPackets(){
        Message<NettyServerHandler> message;
        while ((message = this.messages.poll())!=null){
            this.process(message);
        }
    }

    private void process(Message<NettyServerHandler> message){
        message.process(this);
    }

    public void checkAndResetBKId(int id){
        if (this.lastBgId != -1 && id == this.lastBgId && !this.lastBgProcessed) {
            this.lastBgProcessed = true;
            this.lastBgId = -1;
        }
    }
}
