package com.pichs.xsql.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

public class StandardDatabase implements Database {

    private final SQLiteDatabase delegate;

    public StandardDatabase(SQLiteDatabase delegate) {
        this.delegate = delegate;
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return delegate.rawQuery(sql, selectionArgs);
    }

    @Override
    public void execSQL(String sql) throws SQLException {
        delegate.execSQL(sql);
    }

    @Override
    public void beginTransaction() {
        delegate.beginTransaction();
    }

    @Override
    public void endTransaction() {
        delegate.endTransaction();
    }

    @Override
    public boolean inTransaction() {
        return delegate.inTransaction();
    }


    @Override
    public void setTransactionSuccessful() {
        delegate.setTransactionSuccessful();
    }

    @Override
    public void execSQL(String sql, Object[] bindArgs) throws android.database.SQLException {
        delegate.execSQL(sql, bindArgs);
    }

    @Override
    public DatabaseStatement compileStatement(String sql) {
        return new StandardDatabaseStatement(delegate.compileStatement(sql));
    }

    @Override
    public boolean isDbLockedByCurrentThread() {
        return delegate.isDbLockedByCurrentThread();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public SQLiteDatabase getSQLiteDatabase() {
        return delegate;
    }

}
