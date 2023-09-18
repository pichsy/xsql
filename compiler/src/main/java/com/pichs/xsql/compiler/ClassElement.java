package com.pichs.xsql.compiler;

import com.pichs.xsql.annotation.XSqlField;
import com.pichs.xsql.annotation.XSqlTable;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ClassElement {

    // 类名
    private String className;

    // 注释上的表名
    private String annTableName;


    private TypeElement typeElement;

    private List<FieldElement> mFieldElements;

    public ClassElement(TypeElement typeElement) {
        try {
            this.typeElement = typeElement;
            mFieldElements = new ArrayList<>();
            className = typeElement.getSimpleName().toString();

//            Class<?> tableClass = Class.forName("com.pichs.xsql.annotation.XSqlTable");

            annTableName = typeElement.getAnnotation(XSqlTable.class).value();
            XSqlProcessor.print("annTableName: " + annTableName);
            XSqlProcessor.print("className: " + className);

            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            if (enclosedElements != null) {
                for (Element element : enclosedElements) {
                    if (element.getKind() == ElementKind.FIELD) {
                        VariableElement varElement = (VariableElement) element;
                        XSqlField sqlField = varElement.getAnnotation(XSqlField.class);
                        if (sqlField != null) {
                            String fieldAnnName = sqlField.value();
                            if (!"".equals(fieldAnnName)) {
                                FieldElement fieldElement = new FieldElement(varElement, fieldAnnName);
                                mFieldElements.add(fieldElement);
                                XSqlProcessor.print("增加了一个FieldElement: " + fieldElement.getFieldName());

                            }
                        }
                    }
                }
            }
            XSqlProcessor.print("mFieldElements: size :  " + mFieldElements.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAnnTableName() {
        return annTableName;
    }

    public void setAnnTableName(String annTableName) {
        this.annTableName = annTableName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public List<FieldElement> getFieldElements() {
        return mFieldElements;
    }

    public void setFieldElements(List<FieldElement> fieldElements) {
        mFieldElements = fieldElements;
    }
}
