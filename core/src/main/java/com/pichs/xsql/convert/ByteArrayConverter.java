package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

public class ByteArrayConverter implements BaseConverter<byte[]> {

    @Override
    public byte[] getFieldValue(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getBlob(index);
    }

    @Override
    public Object fieldValue2DbValue(byte[] fieldValue) {
        return fieldValue;
    }

    @Override
    public SqlColumnType getSqlColumnType() {
        return SqlColumnType.BLOB;
    }
}
