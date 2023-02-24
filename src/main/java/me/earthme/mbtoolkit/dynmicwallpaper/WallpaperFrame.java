package me.earthme.mbtoolkit.dynmicwallpaper;

import javax.swing.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 动态壁纸的窗口，目前还没完成
 */
@Deprecated
public class WallpaperFrame extends JFrame implements Runnable{
    private final String filePath;
    private final Executor asyncWorker = Executors.newSingleThreadExecutor();

    public void init(){

    }

    public WallpaperFrame(String filePath){
        this.filePath = filePath;
        this.setTitle("WFA_1033");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.asyncWorker.execute(()->{
            this.setFocusable(false);
            this.setUndecorated(true);
            //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            //Rectangle bounds = new Rectangle(screenSize);
            this.setBounds(200,200,10,10);
            this.setVisible(true);
        });
    }

    @Override
    public void run() {

    }

    public void stop(){
        this.dispose();
    }
}
