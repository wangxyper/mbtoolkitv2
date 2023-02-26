package me.earthme.mbtoolkit.util;

public class StrUtil {
    public static String mergeWithSpace(String... strings){
        final StringBuilder sb = new StringBuilder();
        for (String s : strings){
            sb.append(s).append(" ");
        }
        return sb.toString();
    }
}
