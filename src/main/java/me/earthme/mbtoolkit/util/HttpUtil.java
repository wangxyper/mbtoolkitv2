package me.earthme.mbtoolkit.util;

import org.apache.logging.log4j.LogManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static byte[] readInputStreamToByte(InputStream inputStream) throws IOException {
        byte[] buffer = new byte['Ѐ'];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static byte[] getBytes(String url1){
        try{
            LogManager.getLogger().info("Downloading:{}",url1);
            final URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0(X11;Linuxi686;U;)Gecko/20070322Kazehakase/0.4.5");
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(3000);
            connection.connect();
            try{
                if (connection.getResponseCode() == 200){
                    return readInputStreamToByte(connection.getInputStream());
                }else{
                    LogManager.getLogger().error("Response code:{} Response:{}",connection.getResponseCode(),new String(readInputStreamToByte(connection.getInputStream())));
                }
            }finally {
                connection.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
