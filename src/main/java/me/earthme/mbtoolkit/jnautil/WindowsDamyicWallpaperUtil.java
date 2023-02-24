package me.earthme.mbtoolkit.jnautil;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import me.earthme.mbtoolkit.dynmicwallpaper.WallpaperFrame;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static com.sun.jna.platform.win32.WinUser.SMTO_NORMAL;
import static com.sun.jna.platform.win32.WinUser.SW_HIDE;

/**
 * 动态壁纸的窗口，目前还没完成
 */
@Deprecated
public class WindowsDamyicWallpaperUtil {
    private static final Executor taskPool = Executors.newSingleThreadExecutor();
    private static volatile boolean hasStopped = false;
    private static WallpaperFrame currentFrame = null;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(()-> hasStopped = true));
    }

    private static void runEventLoopAsync(WallpaperFrame loopTask){
        CompletableFuture.runAsync(() -> {
            while (!hasStopped) {
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
        final WinDef.HWND[] hide = {null};

        new Thread(new WallpaperFrame("")).start();

        User32 user32 = User32.INSTANCE;
        WinDef.HWND windowHandle = user32.FindWindow("Progman", null);

        user32.SendMessageTimeout(windowHandle, 0x052c, null, null, SMTO_NORMAL,
                0x3e8, null);
        user32.EnumWindows((hWnd, data) -> {
            WinDef.HWND defview = user32.FindWindowEx(hWnd, null, "SHELLDLL_DefView", null);
            if (defview != null) {
                hide[0] = user32.FindWindowEx(null, hWnd, "WorkerW", null);
            }
            return true;
        }, null);
        user32.ShowWindow(hide[0], SW_HIDE);
        WinDef.HWND hwnd = user32.FindWindow(null, "BG_FRAME");
        user32.SetParent(hwnd,windowHandle);
    }
}
