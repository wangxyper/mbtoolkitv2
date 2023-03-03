package me.earthme.mbtoolkit;

import me.earthme.mbtoolkit.backgroundlocking.BackgroundforceBackendThread;
import me.earthme.mbtoolkit.network.ClientInstance;
import me.earthme.mbtoolkit.network.ServerInstance;
import me.earthme.mbtoolkit.server.ServerConsole;
import me.earthme.mbtoolkit.server.data.ConfigFile;
import me.earthme.mbtoolkit.server.manager.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final BackgroundforceBackendThread backgroundForceThread = new BackgroundforceBackendThread();
    private static ServerInstance server;
    private static ClientInstance client;
    private static final Logger logger = LogManager.getLogger();
    private static final ServerConsole console;

    static {
        ServerConsole console1;
        try {
            console1 = new ServerConsole();
        } catch (IOException e) {
            e.printStackTrace();
            console1 = null;
        }
        console = console1;
    }

    public static ServerInstance getServer() {
        return server;
    }

    public static ClientInstance getClient() {
        return client;
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
        server = new ServerInstance();
        server.start(new InetSocketAddress(file.getRemoteServerHostName(),file.getRemoteServerPort()));
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        if (console!=null){
            console.blockAndRunConsole();
            System.exit(0);
        }else {
            logger.error("Can not start console!Exiting!");
            System.exit(-1);
        }
    }

    public static void initClientSide(ConfigFile file){
        logger.info("Client side init");
        client = new ClientInstance();
        backgroundForceThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            client.shutdown();
            backgroundForceThread.stopRunning();
        }));
        try {
            client.connect(new InetSocketAddress(file.getRemoteServerHostName(),file.getRemoteServerPort()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}