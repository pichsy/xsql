package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

public class BooleanConverter implements BaseConverter<Boolean> {

    @Override
    public Boolean getFieldValue(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index) == 1;
    }

    @Override
    public Object fieldValue2DbValue(Boolean fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue ? 1 : 0;
    }

    @Override
    public SqlColumnType getSqlColumnType() {
        return SqlColumnType.INTEGER;
    }
}
