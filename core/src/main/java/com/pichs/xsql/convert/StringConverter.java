package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

public class StringConverter implements BaseConverter<String> {

    @Override
    public String getFieldValue(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    @Override
    public Object fieldValue2DbValue(String fieldValue) {
        return fieldValue;
    }

    @Override
    public SqlColumnType getSqlColumnType() {
        return SqlColumnType.TEXT;
    }
}
