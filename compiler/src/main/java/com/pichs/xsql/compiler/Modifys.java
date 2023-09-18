package com.pichs.xsql.compiler;

import java.util.EnumSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * 生成类
 */
public class Modifys {
    /**
     * getNone
     * @return 修饰符
     */
    public static Set<Modifier> getNone() {
        return EnumSet.noneOf(Modifier.class);
    }
    /**
     * getDefault
     * @return 修饰符
     */
    public static Set<Modifier> getDefault() {
        return EnumSet.of(Modifier.DEFAULT);
    }
    /**
     * getPublic
     * @return 修饰符
     */
    public static Set<Modifier> getPublic() {
        return EnumSet.of(Modifier.PUBLIC);
    }
    /**
     * getStatic
     * @return 修饰符
     */
    public static Set<Modifier> getStatic() {
        return EnumSet.of(Modifier.STATIC);
    }
    /**
     * getPublicStatic
     * @return 修饰符
     */
    public static Set<Modifier> getPublicStatic() {
        return EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
    }
    /**
     * getPublicFinal
     * @return 修饰符
     */
    public static Set<Modifier> getPublicFinal() {
        return EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
    }

    /**
     * getPublicStaticFinal
     * @return 修饰符
     */
    public static Set<Modifier> getPublicStaticFinal() {
        return EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }


}
