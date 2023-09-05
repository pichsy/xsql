package com.pichs.xsql.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pichs.xsql.utils.XSqlLog;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    protected final Context context;
    protected final String databaseName;

    public DatabaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.context = context;
        this.databaseName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreate(wrap(db));
    }

    public void onCreate(Database db) {
        XSqlLog.d( "onCreate: dbName：" + databaseName);
    }

    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        XSqlLog.d("onUpgrade: dbName：" + databaseName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(wrap(db), oldVersion, newVersion);
    }

    public Database getReadDatabase() {
        return wrap(getReadableDatabase());
    }

    public Database getWriteDatabase() {
        return wrap(getWritableDatabase());
    }

    protected Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new StandardDatabase(sqLiteDatabase);
    }

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
