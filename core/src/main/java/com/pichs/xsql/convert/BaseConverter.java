package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

public interface BaseConverter<T> {

    /**
     * 获取Field的值
     * @param cursor Cursor对象
     * @param index 角标
     * @return 泛型对象
     */
    T getFieldValue(final Cursor cursor, int index);

    /**
     * 获取field对应的数据库的值
     * @param fieldValue 值
     * @return {@link Object}
     */
    Object fieldValue2DbValue(T fieldValue);

    /**
     * 获取SqlColumnType数据库字段的类型
     * @return {@link SqlColumnType}
     */
    SqlColumnType getSqlColumnType();

}
