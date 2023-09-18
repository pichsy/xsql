package com.pichs.xsql.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pichs.xsql.utils.XSqlLog;

/**
 * 数据库操作类
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    /**
     * context
     */
    protected final Context context;
    /**
     * 数据库名称
     */
    protected final String databaseName;

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param name    数据库名称
     * @param version 数据库版本号
     */
    public DatabaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.context = context;
        this.databaseName = name;
    }

    /**
     * onCreate
     * 创建数据库
     *
     * @param db 数据库对象
     * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreate(wrap(db));
    }

    /**
     * onCreate
     * 创建数据库
     *
     * @param db 数据库对象
     */
    public void onCreate(Database db) {
        XSqlLog.d("onCreate: dbName：" + databaseName);
    }

    /**
     * onUpgrade
     * 数据库升级
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
     */
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        XSqlLog.d("onUpgrade: dbName：" + databaseName);
    }

    /**
     * onUpgrade
     * 数据库升级
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(wrap(db), oldVersion, newVersion);
    }

    /**
     * getReadDatabase
     * 获取可读数据库
     *
     * @return {@link Database}
     */
    public Database getReadDatabase() {
        return wrap(getReadableDatabase());
    }

    /**
     * getWriteDatabase
     * 获取可写数据库
     *
     * @return {@link Database}
     */
    public Database getWriteDatabase() {
        return wrap(getWritableDatabase());
    }

    /**
     * wrap
     * 包装数据库对象
     *
     * @param sqLiteDatabase 数据库对象
     * @return {@link Database}
     */
    protected Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new StandardDatabase(sqLiteDatabase);
    }

    /**
     * onOpen
     * 打开数据库
     *
     * @param db 数据库对象
     * @see SQLiteOpenHelper#onOpen(SQLiteDatabase)
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        onOpen(wrap(db));
    }

    /**
     * @param db 数据库对象
     */
    public void onOpen(Database db) {
        // Do nothing by default
    }

}
