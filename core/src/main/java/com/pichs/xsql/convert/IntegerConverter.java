package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

public class IntegerConverter implements BaseConverter<Integer> {

    @Override
    public Integer getFieldValue(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index);
    }

    @Override
    public Object fieldValue2DbValue(Integer fieldValue) {
        return fieldValue;
    }

    @Override
    public SqlColumnType getSqlColumnType() {
        return SqlColumnType.INTEGER;
    }
}
