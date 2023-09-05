package com.pichs.xsql.compiler;

import javax.lang.model.element.VariableElement;

public class FieldElement {

    private VariableElement mVariableElement;
    private String annFieldName;
    private String fieldName;

    public FieldElement(VariableElement variableElement, String annName) {
        mVariableElement = variableElement;
        this.annFieldName = annName;
        fieldName = variableElement.getSimpleName().toString();
    }

    public VariableElement getVariableElement() {
        return mVariableElement;
    }

    public void setVariableElement(VariableElement variableElement) {
        mVariableElement = variableElement;
    }

    public String getAnnFieldName() {
        return annFieldName;
    }

    public void setAnnFieldName(String annFieldName) {
        this.annFieldName = annFieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
