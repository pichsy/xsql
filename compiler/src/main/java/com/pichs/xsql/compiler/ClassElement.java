package com.pichs.xsql.compiler;

import com.pichs.xsql.annotation.XSqlField;
import com.pichs.xsql.annotation.XSqlTable;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * ClassElement
 */
public class ClassElement {

    /**
     * 生成的类名
      */
    private String className;

    /**
     * 生成的表名
     */
    private String annTableName;

    /**
     * 类元素
     */
    private TypeElement typeElement;

    /**
     * 字段元素
     */
    private List<FieldElement> mFieldElements;

    /**
     * @param typeElement  类元素
     */
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

    /**
     * getClassName
     * @return 类名
     */
    public String getClassName() {
        return className;
    }

    /**
     * setClassName
     * @param className 类名
     *
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * getAnnTableName
     * @return 表名
     */
    public String getAnnTableName() {
        return annTableName;
    }

    /**
     * setAnnTableName
     * @param annTableName 表名
     */
    public void setAnnTableName(String annTableName) {
        this.annTableName = annTableName;
    }

    /**
     * getTypeElement
     * @return 类元素
     */
    public TypeElement getTypeElement() {
        return typeElement;
    }

    /**
     * setTypeElement
     * @param typeElement 类元素
     */
    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    /**
     * getFieldElements
     * @return 字段元素
     */
    public List<FieldElement> getFieldElements() {
        return mFieldElements;
    }

    /**
     * setFieldElements
     * @param fieldElements 字段元素
     */
    public void setFieldElements(List<FieldElement> fieldElements) {
        mFieldElements = fieldElements;
    }
}
