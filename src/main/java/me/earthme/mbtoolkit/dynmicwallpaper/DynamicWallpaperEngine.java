package me.earthme.mbtoolkit.dynmicwallpaper;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

import static com.sun.jna.platform.win32.WinUser.SMTO_NORMAL;
import static com.sun.jna.platform.win32.WinUser.SW_HIDE;

/**
 * This part has not completed yet.
 */
@Deprecated
public class DynamicWallpaperEngine extends JFrame implements Runnable{
    private static final Random randomGen = new Random();
    private static final Logger logger = LogManager.getLogger();
    private final String filePath;
    private final JPanel panel = new JPanel();

    public void initAndBlock(){
        final String titleName = "DBG-"+randomGen.nextInt();
        logger.info("Generated title name:{}",titleName);
        this.setTitle(titleName);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setFocusable(false);
        this.setUndecorated(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        this.getContentPane().add(panel);

        ImageIcon icon = new ImageIcon(this.filePath);
        final JLabel label = new JLabel();
        label.setIcon(icon);
        this.panel.add(label);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(d.width, d.height);
        this.setLocation(0,0);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                logger.info("JNA User32 calling");
                User32 user32 = User32.INSTANCE;
                WinDef.HWND windowHandle = user32.FindWindow("Progman", null);
                user32.SendMessageTimeout(windowHandle, 0x052c, null, null, SMTO_NORMAL, 0x3e8, null);
                user32.EnumWindows((hWnd, data) -> {
                    WinDef.HWND defview = user32.FindWindowEx(hWnd, null, "SHELLDLL_DefView", null);
                    if (defview != null) {
                        final WinDef.HWND hide = user32.FindWindowEx(null, hWnd, "WorkerW", null);
                        user32.ShowWindow(hide, SW_HIDE);
                        WinDef.HWND hwnd = user32.FindWindow(null,getTitle());
                        user32.SetParent(hwnd,windowHandle);
                    }
                    return true;
                }, null);
            }
        });
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new DynamicWallpaperEngine("test.gif").initAndBlock();
    }

    public DynamicWallpaperEngine(String filePath){
        this.filePath = filePath;
    }

    @Override
    public void run() {
        long startedTime = System.nanoTime();

        try {
            this.doUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long timeUsed = System.nanoTime() - startedTime;

        if (timeUsed <= 50_000_000L) {
            LockSupport.parkNanos(timeUsed);
        }
    }

    private void doUpdate(){
    }
}
