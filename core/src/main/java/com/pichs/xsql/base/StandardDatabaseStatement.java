package com.pichs.xsql.base;

import android.database.sqlite.SQLiteStatement;

public class StandardDatabaseStatement implements DatabaseStatement {
    private final SQLiteStatement delegate;

    public StandardDatabaseStatement(SQLiteStatement delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute() {
        delegate.execute();
    }

    @Override
    public long simpleQueryForLong() {
        return delegate.simpleQueryForLong();
    }

    @Override
    public void bindNull(int index) {
        delegate.bindNull(index);
    }

    @Override
    public long executeInsert() {
        return delegate.executeInsert();
    }

    @Override
    public void bindString(int index, String value) {
        delegate.bindString(index, value);
    }

    @Override
    public void bindBlob(int index, byte[] value) {
        delegate.bindBlob(index, value);
    }

    @Override
    public void bindLong(int index, long value) {
        delegate.bindLong(index, value);
    }

    @Override
    public void clearBindings() {
        delegate.clearBindings();
    }

    @Override
    public void bindDouble(int index, double value) {
        delegate.bindDouble(index, value);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public SQLiteStatement getSQLiteStatement() {
        return delegate;
    }

}