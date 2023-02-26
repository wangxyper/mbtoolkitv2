package me.earthme.mbtoolkit.server.data;

import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

public class UserDataEntry {
    private final UUID userUUID;
    private boolean isAdmin;
    private volatile transient boolean isDirty = false;

    public UserDataEntry(UUID userUUID, boolean isAdmin) {
        this.userUUID = userUUID;
        this.isAdmin = isAdmin;
    }

    public void setDirty(boolean dirty){
        this.isDirty = dirty;
    }

    public void setToAdmin(boolean isAdmin){
        this.isDirty = true;
        this.isAdmin = isAdmin;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public UUID getUserUUID() {
        return this.userUUID;
    }

    public void writeToFile(File folder,String name){
        final File fileEntry = folder == null ? new File(name) : new File(folder, name);
        try {
            Files.write(fileEntry.toPath(), JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(File fileEntry){
        try {
            Files.write(fileEntry.toPath(), JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserDataEntry readFromFile(File folder,String name){
        try {
            final File fileEntry = folder == null ? new File(name) : new File(folder,name);
            if (fileEntry.exists()){
                final byte[] bytes = Files.readAllBytes(fileEntry.toPath());
                return JSONObject.parseObject(new String(bytes,StandardCharsets.UTF_8), UserDataEntry.class);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static UserDataEntry readFromFile(File fileEntry){
        try {
            if (fileEntry.exists()){
                final byte[] bytes = Files.readAllBytes(fileEntry.toPath());
                return JSONObject.parseObject(new String(bytes,StandardCharsets.UTF_8), UserDataEntry.class);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        new UserDataEntry(UUID.randomUUID(),true).writeToFile(new File("test"),"test.json");
    }
}
