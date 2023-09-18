package com.pichs.xsql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 唯一键注解，只能用在字段上
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XSqlPrimaryKey {
    /**
     * 是否唯一键
     * @return 是否唯一键
     */
    boolean value() default true;
}
