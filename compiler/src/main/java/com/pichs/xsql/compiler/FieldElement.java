package com.pichs.xsql.compiler;

import javax.lang.model.element.VariableElement;

/**
 * FieldElement
 */
public class FieldElement {

    private VariableElement mVariableElement;
    private String annFieldName;
    private String fieldName;
    /**
     * @param variableElement 变量元素
     * @param annName 字段名
     */
    public FieldElement(VariableElement variableElement, String annName) {
        mVariableElement = variableElement;
        this.annFieldName = annName;
        fieldName = variableElement.getSimpleName().toString();
    }
    /**
     * getVariableElement
     * @return 变量元素
     */
    public VariableElement getVariableElement() {
        return mVariableElement;
    }

    /**
     * setVariableElement
     * @param variableElement 变量元素
     */
    public void setVariableElement(VariableElement variableElement) {
        mVariableElement = variableElement;
    }

    /**
     * getAnnFieldName
     * @return 字段名
     */
    public String getAnnFieldName() {
        return annFieldName;
    }

    /**
     * setAnnFieldName
     * @param annFieldName 字段名
     */
    public void setAnnFieldName(String annFieldName) {
        this.annFieldName = annFieldName;
    }

    /**
     * getFieldName
     * @return 字段名
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * setFieldName
     * @param fieldName 字段名
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
