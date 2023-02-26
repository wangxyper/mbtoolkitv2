package me.earthme.mbtoolkit;

import me.earthme.mbtoolkit.backgroundlocking.BackgroundforceBackendThread;
import me.earthme.mbtoolkit.network.NettySocketClient;
import me.earthme.mbtoolkit.network.NetworkSocketServer;
import me.earthme.mbtoolkit.server.ServerConsole;
import me.earthme.mbtoolkit.server.data.ConfigFile;
import me.earthme.mbtoolkit.server.manager.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class ServerMain {
    private static final BackgroundforceBackendThread backgroundForceThread = new BackgroundforceBackendThread();
    private static final NetworkSocketServer server = new NetworkSocketServer();
    private static final NettySocketClient client = new NettySocketClient();
    private static final Logger logger = LogManager.getLogger();
    private static final ServerConsole console;

    static {
        try {
            console = new ServerConsole();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static BackgroundforceBackendThread getBackgroundForceThread(){
        return backgroundForceThread;
    }

    public static void main(String[] args) {
        ConfigManager.load();
        final ConfigFile loaded = ConfigManager.getConfigFile();
        if (!loaded.isServerSide()){
            initClientSide(loaded);
        }else {
            initServerSide(loaded);
        }
    }

    public static void initServerSide(ConfigFile file){
        logger.info("Server side init");
        server.start(new InetSocketAddress(file.getRemoteServerHostName(),file.getRemoteServerPort()));
        console.blockAndRunConsole();
        System.exit(0);
    }

    public static void initClientSide(ConfigFile file){
        logger.info("Client side init");
        backgroundForceThread.start();
        try {
            client.connect(new InetSocketAddress(file.getRemoteServerHostName(),file.getRemoteServerPort()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}