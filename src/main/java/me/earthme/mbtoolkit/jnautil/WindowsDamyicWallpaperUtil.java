package me.earthme.mbtoolkit.jnautil;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import me.earthme.mbtoolkit.dynmicwallpaper.WallpaperFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static com.sun.jna.platform.win32.WinUser.*;

@Deprecated
public class WindowsDamyicWallpaperUtil {
    private static final Executor taskPool = Executors.newSingleThreadExecutor();
    private static WallpaperFrame currentFrame = null;

    private static void runEventLoopAsync(WallpaperFrame loopTask){
        CompletableFuture.runAsync(() -> {
            while (loopTask.isRunning()) {
                long startedTime = System.nanoTime();

                try {
                    loopTask.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final long timeUsed = System.nanoTime() - startedTime;

                if (timeUsed <= 50_000_000L) {
                    LockSupport.parkNanos(timeUsed);
                }
            }
        }, taskPool);
    }

    public synchronized static boolean checkRunning(){
        return currentFrame != null;
    }

    private static void checkAndStop(){
        if (currentFrame!=null){
            currentFrame.stop();
        }
    }

    public static void main(String[] args) {
            runNew("test");
    }

    public synchronized static void runNew(String wallpaperPath){
        checkAndStop();
        final WallpaperFrame wf = new WallpaperFrame(wallpaperPath);
        wf.init();
        wf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                User32 user32 = User32.INSTANCE;
                WinDef.HWND windowHandle = user32.FindWindow("Progman", null);
                user32.SendMessageTimeout(windowHandle, 0x052c, null, null, SMTO_NORMAL, 0x3e8, null);
                user32.EnumWindows((hWnd, data) -> {
                    WinDef.HWND defview = user32.FindWindowEx(hWnd, null, "SHELLDLL_DefView", null);
                    if (defview != null) {
                        final HWND hide = user32.FindWindowEx(null, hWnd, "WorkerW", null);
                        user32.ShowWindow(hide, SW_HIDE);
                        WinDef.HWND hwnd = user32.FindWindow(null, wf.getTitle());
                        user32.SetParent(hwnd,windowHandle);
                    }
                    return true;
                }, null);
            }
        });
    }
}
