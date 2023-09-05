package com.pichs.xsql.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface Database {

    Cursor rawQuery(String sql, String[] selectionArgs);

    void execSQL(String sql) throws java.sql.SQLException;

    void beginTransaction();

    void endTransaction();

    boolean inTransaction();

    void setTransactionSuccessful();

    void execSQL(String sql, Object[] bindArgs) throws java.sql.SQLException;

    DatabaseStatement compileStatement(String sql);

    boolean isDbLockedByCurrentThread();

    void close();

    boolean isOpen();

    public SQLiteDatabase getSQLiteDatabase();
}
