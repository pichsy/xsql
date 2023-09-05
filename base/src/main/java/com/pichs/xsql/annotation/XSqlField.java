package com.pichs.xsql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记数据库字段
 * 只支持 包装类，和 String-> 以下几种：
 * Double，Float，Integer，Long， Boolean，String
 * 不支持其他类型，以上几种足够用了，其他类型没必要。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XSqlField {
    String value();
}
