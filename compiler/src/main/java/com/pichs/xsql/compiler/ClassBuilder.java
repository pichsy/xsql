package com.pichs.xsql.compiler;


import com.pichs.xsql.annotation.XSqlTable;
import com.pichs.xsql.compiler.XSqlProcessor;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class ClassBuilder {

    public static final String rootClassName = "XSqlProperties";
    public static final String rootPackageName = "com.pichs.xsql.property";
    private List<ClassElement> mClassElements;
    // 包名
    private String qualifiedName;

    public ClassBuilder(RoundEnvironment roundEnvironment) {
        mClassElements = new ArrayList<>();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(XSqlTable.class);

        if (elements != null) {
            for (Element element : elements) {
                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement) element;
                    if (typeElement.getAnnotation(XSqlTable.class) == null) {
                        continue;
                    }
                    ClassElement classElement = new ClassElement(typeElement);
                    mClassElements.add(classElement);
                    XSqlProcessor.print("增加了一个ClassElement: " + element.getSimpleName());
                }
            }
        }
    }

    public void builder(Filer filer) throws IOException {
        JavaWriter jw = new JavaWriter(filer.createSourceFile(rootPackageName + "." + rootClassName).openWriter());
        jw.emitPackage(rootPackageName);
        jw.beginType(rootClassName, KindType.CLASS, Modifys.getPublicFinal());
        jw.emitEmptyLine();
        XSqlProcessor.print("mClassElements: size :  " + mClassElements.size());
        for (ClassElement classElement : mClassElements) {
            jw.beginType(classElement.getClassName(), KindType.CLASS, Modifys.getPublicStaticFinal());
            jw.emitEmptyLine();
            List<FieldElement> fieldElements = classElement.getFieldElements();
            for (FieldElement fieldElement : fieldElements) {
                jw.emitField(KindType.STRING, fieldElement.getFieldName(), Modifys.getPublicStaticFinal(), Utils.formatString(fieldElement.getAnnFieldName()));
                jw.emitEmptyLine();
            }
            jw.endType();
            jw.emitEmptyLine();
        }
        jw.endType();
        jw.close();
    }

}
