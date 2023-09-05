package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

public class DoubleConverter implements BaseConverter<Double> {

    @Override
    public Double getFieldValue(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    @Override
    public Object fieldValue2DbValue(Double fieldValue) {
        return fieldValue;
    }

    @Override
    public SqlColumnType getSqlColumnType() {
        return SqlColumnType.REAL;
    }
}
