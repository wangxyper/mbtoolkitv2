package me.earthme.mbtoolkit.backgroundlocking;

import me.earthme.mbtoolkit.jnautil.WindowsDesktopWallpaperUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class BackgroundforceThread extends Thread{
    private static final Logger logger = LogManager.getLogger();
    private final AtomicReference<String> currentWallpaper = new AtomicReference<>();
    private long lastUpdatedTime;
    private volatile boolean running = true;
    private volatile Thread awaitIngThread;

    @Override
    public void run(){
        while (this.running){
            this.lastUpdatedTime = System.nanoTime();

            try {
                this.doUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }

            final long usedTime = System.nanoTime() - this.lastUpdatedTime;
            if (usedTime < 100_000_000L){
                LockSupport.parkNanos(100_000_000L - usedTime);
            }
        }
        if (this.awaitIngThread!=null){
            LockSupport.unpark(this.awaitIngThread);
        }
    }

    public long getLastUpdatedTime(){
        return this.lastUpdatedTime;
    }

    public void awaitExit(){
        this.awaitIngThread = Thread.currentThread();
        LockSupport.park();
    }

    public void stopRunning(){
        this.running = false;
    }

    public void setForcing(String path){
        this.currentWallpaper.set(path);
    }

    private void doUpdate() {
        if (this.currentWallpaper.get()!=null){
            WindowsDesktopWallpaperUtil.setTo(this.currentWallpaper.get());
        }
    }
}
