package me.earthme.mbtoolkit.jnautil;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WindowsDesktopWallpaperUtil {
    private static final Executor asyncSettingExecutor = Executors.newSingleThreadExecutor();

    private interface MyUser32 extends StdCallLibrary {

        MyUser32 INSTANCE = Native.load("user32", MyUser32.class);
        boolean SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);
    }

    public static CompletableFuture<Void> setAsync(String imgPath){
        return CompletableFuture.runAsync(()-> setTo(imgPath),asyncSettingExecutor);
    }

    public static boolean setTo(String imgPath){
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper", imgPath);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "WallpaperStyle", "10");
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "TileWallpaper", "0");

        int SPI_SETDESKWALLPAPER = 0x14;
        int SPIF_UPDATEINIFILE = 0x01;
        int SPIF_SENDWININICHANGE = 0x02;

        return MyUser32.INSTANCE.SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0, imgPath, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE );
    }

}
