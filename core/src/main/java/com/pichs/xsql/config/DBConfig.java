package com.pichs.xsql.config;

import android.content.Context;

/**
 * DBConfig 设置数据库创建时的参数
 * 数据库保存路劲为： data/data/${applicationId}/files/${dbDir}/${dbName}
 * dbDir，数据库路劲
 * dbName，数据库名称
 * newVersion 数据库版本号
 */
public class DBConfig {

    private Context mContext;
    // 数据库会保存到 dbDir/${dbPath}/${dbName} 目录下
    /**
     * dbDir规则
     * 数据库放在data/data/${applicationId}/files/${dbDir}/${dbName}目录下
     * 所以dbDir应该为 xxx/xxx 或者 xxx 格式。：  / 代表文件目录层级
     */
    private String dbDir;
    private String dbName;
    private int newVersion;

    public DBConfig setContext(Context context) {
        mContext = context;
        return this;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 数据库名字，请不要使用 含有 / 的名字。
     * @param name 请不要使用 含有 / 的名字。
     * @return String
     */
    public DBConfig setDBName(String name) {
        dbName = name;
        return this;
    }

    public String getDbDir() {
        return dbDir;
    }

    /**
     * dbDir规则
     * 数据库放在data/data/${applicationId}/files/${dbDir}/${dbName}目录下
     * @param dbDir dbDir应该为 xxx/xxx 或者 xxx 格式。：  / 代表文件目录层级
     */
    public DBConfig setDbDir(String dbDir) {
        this.dbDir = dbDir;
        return this;
    }

    /**
     * 版本可以不设置，默认是1，设置了好像也没啥用，补位-待扩展吧。
     * @param version 1.0
     * @return DBConfig
     */
    public DBConfig setVersion(int version) {
        newVersion = version;
        return this;
    }

    /**
     * 数据库名字，请不要使用 含有 / 的名字。
     * @return String
     */
    public String getDBName() {
        return dbName;
    }

    public int getVersion() {
        return newVersion;
    }


}
