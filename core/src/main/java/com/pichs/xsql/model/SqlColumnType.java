package com.pichs.xsql.model;

/**
 * 字段名和字段对象
 */
public enum SqlColumnType {

    /**
     * int
     */
    INTEGER("INTEGER"),
    /**
     * long
     */
    REAL("REAL"),
    /**
     * String
     */
    TEXT("TEXT"),
    /**
     * byte[]
     */
    BLOB("BLOB");

    /**
     * 枚举类型
     */
    private String value;

    /**
     * 构造方法
     * @param value 枚举类型
     */
    SqlColumnType(String value) {
        this.value = value;
    }

    /**
     * 重写toString方法
     * @return 字符串
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * 获取枚举类型
     * @return 枚举类型
     */
    public String getValue() {
        return value;
    }
}
