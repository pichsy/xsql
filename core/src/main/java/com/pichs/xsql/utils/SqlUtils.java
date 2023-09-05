package com.pichs.xsql.utils;

import java.lang.reflect.Field;

public class SqlUtils {

    /**
     * 判断是否是PrimaryKey支持的类型
     * 只支持Long
     *
     * @param field Field对象
     * @return 是否支持，true is support ，false is not support
     */
    public static boolean isPrimaryKeySupportType(Field field) {
        String name = field.getType().getName();
        return name.equals(Long.class.getName());
    }

    /**
     * 判断是否是PrimaryKey支持的类型
     * 只支持Long
     *
     * @param field Field对象
     * @return 是否支持，true is support ，false is not support
     */
    public static boolean isSupportClassType(Field field) {
        String name = field.getType().getName();
        if (name.equals(String.class.getName())) {
            return true;
        }
        if (name.equals(Boolean.class.getName())) {
            return true;
        }
        if (name.equals(Integer.class.getName())) {
            return true;
        }
        if (name.equals(Double.class.getName())) {
            return true;
        }
        if (name.equals(Long.class.getName())) {
            return true;
        }
        if (name.equals(Float.class.getName())) {
            return true;
        }
        return name.equals(byte[].class.getName());
    }
}
