package com.pichs.xsql.convert;

import android.database.Cursor;

import com.pichs.xsql.model.SqlColumnType;

/**
 * 数据库字段类型转换器
 * Double类型转换器
 */
public class DoubleConverter implements BaseConverter<Double> {

    /**
     * getFieldValue
     *
     * @param cursor Cursor对象
     * @param index  角标
     * @return 泛型对象
     */
    @Override
    public Double getFieldValue(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    /**
     * fieldValue2DbValue
     *
     * @param fieldValue 值
     * @return {@link Object}
     */
    @Override
    public Object fieldValue2DbValue(Double fieldValue) {
        return fieldValue;
    }

    /**
     * getSqlColumnType
     * 获取SqlColumnType数据库字段的类型
     *
     * @return {@link SqlColumnType}
     */
    @Override
    public SqlColumnType getSqlColumnType() {
        return SqlColumnType.REAL;
    }
}
