package com.pichs.xsql.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pichs.xsql.config.DBConfig;
import com.pichs.xsql.dao.BaseDao;
import com.pichs.xsql.dao.IBaseDao;

import java.sql.SQLException;

/**
 * 数据库管理类
 */
public class DBManager {

    private static DBManager mInstance;
    private String dbName;
    private DBConfig mDBConfig;
    private int newVersion = 1;

    /**
     * 构造函数
     *
     * @param context  上下文
     * @param dbConfig 配置文件
     */
    public DBManager(Context context, DBConfig dbConfig) {
        dbName = context.getPackageName().replace(".", "-") + ".db";
        if (dbConfig != null) {
            this.mDBConfig = dbConfig;
            String _dbName = dbConfig.getDBName();
            if (!TextUtils.isEmpty(_dbName)) {
                this.dbName = _dbName;
            }
            this.newVersion = Math.max(dbConfig.getVersion(), 1);
        } else {
            mDBConfig = new DBConfig();
        }
        // 校正dbconfig中的数据值默认。
        mDBConfig.setContext(context).setDBName(dbName).setVersion(newVersion);
    }

    /**
     * 获取单利的DBManager
     *
     * @param context  上下文
     * @param dbConfig 配置文件
     * @return DBManager object
     */
    public static DBManager getInstance(Context context, DBConfig dbConfig) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context, dbConfig);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取Dao，数据库操作工具类
     *
     * @param entityClass 实体的Class
     * @param <T>         泛型
     * @return IBaseDao object
     */
    public <T> IBaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao<T> baseDao = new BaseDao<>();
        try {
            baseDao.init(mDBConfig, entityClass);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("XSql", "getBaseDao: " + e.getMessage());
            return null;
        }
        return baseDao;
    }


    /**
     * 返回DBConfig
     *
     * @return DBConfig
     */
    public DBConfig getDBConfig() {
        return mDBConfig;
    }

}
