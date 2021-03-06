package com.pichs.xsql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否唯一键注解，只能用在字段上
 * value = true 表示唯一键
 * value = false 表示不是唯一键
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XSqlUnique {
    /**
     * 是否唯一键
     * @return 是否唯一键
     */
    boolean value() default true;
}
