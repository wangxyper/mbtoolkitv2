package me.earthme.mbtoolkit.util;

import java.security.MessageDigest;

public class MD5Utils {
    private final static String[] strHex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static String getMD5One(byte[] data) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(data);
            for (int j : b) {
                int d = j;
                if (d < 0) {
                    d += 256;
                }
                int d1 = d / 16;
                int d2 = d % 16;
                sb.append(strHex[d1]).append(strHex[d2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
