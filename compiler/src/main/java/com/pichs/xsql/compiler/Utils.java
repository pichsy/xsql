package com.pichs.xsql.compiler;

/**
 * 生成类
 */
public class Utils {
    /**
     * 生成的类名
     * @param s 类名
     *
     * @return 生成的类名
     */
    public static String formatString(String s) {
        return "\"" + s + "\"";
    }
}
