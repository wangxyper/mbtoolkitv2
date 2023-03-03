package me.earthme.mbtoolkit.backgroundlocking;

import me.earthme.mbtoolkit.jnautil.WindowsDesktopWallpaperUtil;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class BackgroundforceThread extends Thread{
    private final AtomicReference<String> currentWallpaper = new AtomicReference<>();

    @Override
    public void run(){
        while (Thread.currentThread().isInterrupted()){
            long lastUpdatedTime = System.nanoTime();

            try {
                this.doUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }

            final long usedTime = System.nanoTime() - lastUpdatedTime;
            if (usedTime < 500_000_000L){
                LockSupport.parkNanos(500_000_000L - usedTime);
            }
        }
    }

    public void stopRunning(){
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
