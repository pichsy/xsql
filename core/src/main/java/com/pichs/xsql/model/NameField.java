package com.pichs.xsql.model;


import java.lang.reflect.Field;

/**
 * 字段名和字段对象
 */
public class NameField {

    private String name;
    private Field field;

    /**
     * 构造方法
     *
     * @param name  字段名
     * @param field 字段对象
     */
    public NameField(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    /**
     * 获取字段名
     * @return 字段名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置字段名
     * @param name 字段名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取字段对象
     * @return 字段对象
     */
    public Field getField() {
        return field;
    }

    /**
     * 设置字段对象
     * @param field 字段对象
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * 重写toString方法
     * @return 字符串
     */
    @Override
    public String toString() {
        return "{" + name + ':' + field + "}";
    }
}
