package me.earthme.mbtoolkit.apis;

import com.google.gson.Gson;
import me.earthme.mbtoolkit.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public class RandECPicResp {
    private static final Gson GSON = new Gson();
    public final int code;
    public final String url;

    public RandECPicResp(int code, String url){
        this.code = code;
        this.url = url;
    }

    public static RandECPicResp getNew(){
        try{
            String api = randomAPI();
            LogManager.getLogger().info("Chose api:{}",api);
            String read = new String(Objects.requireNonNull(HttpUtil.getBytes(api)));
            return GSON.fromJson(read, RandECPicResp.class);
        }catch (Exception e) {
            LogManager.getLogger().error(e.getMessage());
        }
        return null;
    }

    public static String randomAPI(){
        return "https://api.likepoems.com/img/pc?type=json";
    }

    public void saveToFile(File parent,String name){
        try{
            File file = new File(parent,name);
            byte[] buffer = HttpUtil.getBytes(this.url);
            if (buffer!=null){
                Files.write(file.toPath(),buffer);
            }
        }catch (Exception e){
            LogManager.getLogger().error(e);
        }
    }

    public byte[] getBytes(){
        try{
            return HttpUtil.getBytes(this.url);
        }catch (Exception e){
            LogManager.getLogger().error(e);
        }
        return null;
    }
}
