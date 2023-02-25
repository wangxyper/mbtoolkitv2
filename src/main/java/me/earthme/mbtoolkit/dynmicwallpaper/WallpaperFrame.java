package me.earthme.mbtoolkit.dynmicwallpaper;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Deprecated
public class WallpaperFrame extends JFrame implements Runnable{
    private final String filePath;
    private final Executor asyncWorker = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public void init(){
        this.setTitle("WFA_1033");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setFocusable(false);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0,0, (int) (screenSize.getWidth() - 1), (int) (screenSize.getHeight() - 1));
        this.setVisible(true);
    }

    public WallpaperFrame(String filePath){
        this.filePath = filePath;
    }

    @Override
    public void run() {

    }

    public void stop(){
        this.running = false;
        this.dispose();
    }

    public boolean isRunning() {
        return running;
    }
}
