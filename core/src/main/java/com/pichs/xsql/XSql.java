package com.pichs.xsql;

import android.content.Context;

import com.pichs.xsql.config.DBConfig;
import com.pichs.xsql.manager.DBManager;

/**
 * 数据库入口
 */
public class XSql {

    /**
     * 获取数据库管理类
     * 自定义配置
     *
     * @param context  Context
     * @param dbConfig DBConfig
     * @return DBManager
     */
    public static DBManager getDBManager(Context context, DBConfig dbConfig) {
        return DBManager.getInstance(context, dbConfig);
    }

    /**
     * 获取数据库管理类
     * 默认配置
     *
     * @param context Context
     * @return DBManager
     */
    public static DBManager getDBManager(Context context) {
        return DBManager.getInstance(context, null);
    }

}
