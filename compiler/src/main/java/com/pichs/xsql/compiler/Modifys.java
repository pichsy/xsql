package com.pichs.xsql.compiler;

import java.util.EnumSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

public class Modifys {

    public static Set<Modifier> getNone() {
        return EnumSet.noneOf(Modifier.class);
    }

    public static Set<Modifier> getDefault() {
        return EnumSet.of(Modifier.DEFAULT);
    }

    public static Set<Modifier> getPublic() {
        return EnumSet.of(Modifier.PUBLIC);
    }

    public static Set<Modifier> getStatic() {
        return EnumSet.of(Modifier.STATIC);
    }

    public static Set<Modifier> getPublicStatic() {
        return EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
    }

    public static Set<Modifier> getPublicFinal() {
        return EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
    }

    public static Set<Modifier> getPublicStaticFinal() {
        return EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }


}
