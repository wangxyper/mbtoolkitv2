package me.earthme.mbtoolkit.server.data;

import com.alibaba.fastjson2.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

public class ConfigFile {
    private final String remoteServerHostName;
    private final int remoteServerPort;
    private final boolean serverSide;
    private final UUID uuid;
    private final boolean dynamicWallpaperEnabled;

    public ConfigFile(String remoteServerHostName, int remoteServerPort, boolean serverSide, UUID uuid, boolean dynamicWallpaperEnabled) {
        this.remoteServerHostName = remoteServerHostName;
        this.remoteServerPort = remoteServerPort;
        this.serverSide = serverSide;
        this.uuid = uuid;
        this.dynamicWallpaperEnabled = dynamicWallpaperEnabled;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isDynamicWallpaperEnabled() {
        return dynamicWallpaperEnabled;
    }

    public boolean isServerSide() {
        return serverSide;
    }

    public int getRemoteServerPort() {
        return remoteServerPort;
    }

    public String getRemoteServerHostName() {
        return remoteServerHostName;
    }

    public void writeToFile(@NotNull File fileEntry){
        try {
            Files.write(fileEntry.toPath(), JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static @Nullable ConfigFile readFromFile(File fileEntry){
        try {
            if (fileEntry.exists()){
                final byte[] bytes = Files.readAllBytes(fileEntry.toPath());
                return JSONObject.parseObject(new String(bytes,StandardCharsets.UTF_8),ConfigFile.class);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
