package com.pichs.xsql.utils;

/**
 * sql约束
 */
public interface SqlCheck {

    // 不用添加空格直接拼接
    /**
     * 唯一键
     */
    String UNIQUE = " UNIQUE";

    /**
     * 自增键
     */
    String PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT";

    /**
     * 空格
     */
    String BLANK_SPACE = " ";

    /**
     * Where 语句
     */
    String WHERE = " WHERE ";
}
