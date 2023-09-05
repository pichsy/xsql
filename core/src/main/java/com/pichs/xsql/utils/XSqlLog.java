package com.pichs.xsql.utils;

import android.util.Log;

/**
 * @Description:
 * @Author: 吴波
 * @CreateDate: 2020/12/18 15:35
 * @UpdateUser: 吴波
 * @UpdateDate: 2020/12/18 15:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class XSqlLog {

    public volatile static boolean isDebug = true;


    public static void setDebug(boolean isDebug) {
        XSqlLog.isDebug = isDebug;
    }

    public static void d(String message) {
        if (isDebug) {
            if (message != null) {
                Log.d("<====XSQL====>", message);
            }
        }
    }

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
