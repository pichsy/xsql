package com.pichs.xsql.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作接口
 */
public interface Database {

    /**
     * rawQuery
     *
     * @param sql            sql语句
     * @param selectionArgs  参数
     * @return {@link Cursor}
     */
    Cursor rawQuery(String sql, String[] selectionArgs);

    /**
     * execSQL
     *
     * @param sql sql语句
     * @throws java.sql.SQLException 异常
     */
    void execSQL(String sql) throws java.sql.SQLException;


    /**
     * beginTransaction
     */
    void beginTransaction();


    /**
     * endTransaction
     */
    void endTransaction();


    /**
     * inTransaction
     *
     * @return boolean
     */
    boolean inTransaction();


    /**
     * setTransactionSuccessful
     */
    void setTransactionSuccessful();


    /**
     * execSQL
     *
     * @param sql      sql语句
     * @param bindArgs 参数
     * @throws java.sql.SQLException 异常
     */
    void execSQL(String sql, Object[] bindArgs) throws java.sql.SQLException;


    /**
     * compileStatement
     *
     * @param sql sql语句
     * @return {@link DatabaseStatement}
     */
    DatabaseStatement compileStatement(String sql);


    /**
     * isDbLockedByCurrentThread
     *
     * @return boolean
     */
    boolean isDbLockedByCurrentThread();


    /**
     * close
     */
    void close();


    /**
     * isOpen
     *
     * @return boolean
     */
    boolean isOpen();


    /**
     * getSQLiteDatabase
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase getSQLiteDatabase();
}
