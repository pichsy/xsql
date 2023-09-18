package com.pichs.xsql.utils;

import android.util.Log;

/**
 * 日志工具类
 */
public class XSqlLog {

    /**
     * 是否打印日志
     */
    public volatile static boolean isDebug = true;

    /**
     * 设置是否打印日志
     *
     * @param isDebug 是否打印日志
     */
    public static void setDebug(boolean isDebug) {
        XSqlLog.isDebug = isDebug;
    }

    /**
     * 打印日志
     *
     * @param message 日志信息
     */
    public static void d(String message) {
        if (isDebug) {
            if (message != null) {
                Log.d("<====XSQL====>", message);
            }
        }
    }

    /**
     * 打印日志
     *
     * @param message 日志信息
     *                可变参数
     */
    public static void d(String... message) {
        if (isDebug) {
            if (message != null) {
                StringBuilder sb = new StringBuilder();
                for (String s : message) {
                    sb.append(s).append(": :");
                }
                Log.d("<====XSQL====>", sb.toString());
            }
        }
    }


}
