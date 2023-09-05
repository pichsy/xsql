package com.pichs.xsql.model;

public enum SqlColumnType {

    INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT"), BLOB("BLOB");

    /**
     * 枚举类型
     */
    private String value;

    SqlColumnType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}
