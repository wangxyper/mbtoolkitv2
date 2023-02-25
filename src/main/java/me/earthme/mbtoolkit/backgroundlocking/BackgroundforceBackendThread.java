package me.earthme.mbtoolkit.backgroundlocking;

import me.earthme.mbtoolkit.util.MD5Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class BackgroundforceBackendThread extends Thread{
    private static final Logger logger = LogManager.getLogger();
    private final BackgroundforceThread forceWorker = new BackgroundforceThread();
    private final AtomicReference<byte[]> currentBackgroundByteArray = new AtomicReference<>();
    private final Random random = new Random(System.nanoTime());
    private volatile String currentByteArrayMd5;
    private File currentCacheFile;
    private volatile boolean running = true;

    private File currentCacheFileFolder;

    @Override
    public void start(){
        this.currentCacheFileFolder = new File("WallpaperCaches");
        this.initCaches();
        super.start();
    }

    public void initCaches(){
        if (!this.currentCacheFileFolder.mkdir()){
            final File[] files = this.currentCacheFileFolder.listFiles();
            if (files!=null && files.length > 0){
                for (File file : files){
                    file.delete();
                }
                logger.info("Caches cleared");
            }
        }
        this.currentCacheFile = new File(this.currentCacheFileFolder,"cache-"+this.random.nextInt()+".mbcache");
        logger.info("Inited cache file");
    }

    @Override
    public void run(){
        long time;
        while (this.running){
            time = System.nanoTime();
            try {
                this.doUpdate();
            }catch (Exception e){
                logger.error("Error in updating backend:",e);
            }
            final long used = System.nanoTime() - time;

            final long shouldSleep = 1000_000_000 - used;
            if (shouldSleep > 0){
                LockSupport.parkNanos(shouldSleep);
            }else {
                logger.warn("Thread {} used to many time to update wallpaper!Time used:{}",this.getName(),used);
            }
        }
    }

    private void doUpdate() throws Exception {
        if (!this.forceWorker.isAlive()){
            logger.info("Started background force thread");
            this.forceWorker.setForcing(System.getProperty("user.dir")+"\\"+this.currentCacheFile.getPath());
            this.forceWorker.start();
        }

        if (this.currentBackgroundByteArray.get()!=null && this.currentByteArrayMd5 == null){
            logger.info("Inited md5 and cache file");
            this.syncMd5();
            logger.info("Md5 value:{}",this.currentByteArrayMd5);
            this.currentCacheFile.createNewFile();
            Files.write(this.currentCacheFile.toPath(),this.currentBackgroundByteArray.get());
        } else if (this.currentBackgroundByteArray.get() != null) {
            this.syncMd5();
            if (!checkFileMd5()){
                logger.info("Wallpaper file change detected!");
                Files.write(this.currentCacheFile.toPath(),this.currentBackgroundByteArray.get());
            }
        }

        this.syncMd5();
    }

    private void syncMd5(){
        if (this.currentBackgroundByteArray.get()!=null){
            this.currentByteArrayMd5 = MD5Utils.getMD5One(this.currentBackgroundByteArray.get());
        }
    }

    private boolean checkFileMd5() throws IOException {
        if (this.currentCacheFile.exists()){
            final byte[] data = Files.readAllBytes(this.currentCacheFile.toPath());
            return MD5Utils.getMD5One(data).equals(this.currentByteArrayMd5);
        }
        return false;
    }
}
