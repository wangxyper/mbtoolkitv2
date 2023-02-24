package me.earthme.mbtoolkit.backgroundlocking;

import me.earthme.mbtoolkit.jnautil.WindowsDesktopWallpaperUtil;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class BackgroundforceThread extends Thread{
    private final AtomicReference<String> currentWallpaper = new AtomicReference<>();
    private long lastUpdatedTime;
    private volatile boolean running = true;

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
            if ((usedTime - 100_000_000L) > 0){
                LockSupport.parkNanos(usedTime - 100_000_000L);
            }
        }
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
