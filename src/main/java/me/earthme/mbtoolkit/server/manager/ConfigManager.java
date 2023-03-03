package me.earthme.mbtoolkit.server.manager;

import me.earthme.mbtoolkit.server.data.ConfigFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class ConfigManager {
    @NotNull private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    @NotNull private static ConfigFile configFile = new ConfigFile("0.0.0.0",20009,true, UUID.randomUUID(), false);
    @NotNull private static final File configFileEntry = new File("config.json");

    public static void load(){
        try {
            if (!configFileEntry.exists()){
                configFile = new ConfigFile("0.0.0.0",20009,true, UUID.randomUUID(), false);
                configFileEntry.createNewFile();
                configFile.writeToFile(configFileEntry);
                logger.info("Created mew config file");
                return;
            }
            final ConfigFile triedRead = ConfigFile.readFromFile(configFileEntry);
            if (triedRead!=null){
                configFile = triedRead;
            }else{
                throw new IllegalStateException();
            }
            logger.info("Read config file");
        }catch (Exception e){
            logger.error("Can not read config file!");
            e.printStackTrace();
        }
    }

    public static @NotNull ConfigFile getConfigFile() {
        return configFile;
    }
}
