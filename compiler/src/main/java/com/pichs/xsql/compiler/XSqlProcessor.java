package com.pichs.xsql.compiler;

import com.google.auto.service.AutoService;
import com.pichs.xsql.annotation.XSqlTable;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class XSqlProcessor extends AbstractProcessor {

    private Elements mElementsUtils;
    private Types mTypesUtils;
    private Filer mFiler;
    private static Messager mMessager;
    private ClassBuilder classBuilder;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementsUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mTypesUtils = processingEnv.getTypeUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(XSqlTable.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        print("roundEnvironment.errorRaised(): " + roundEnv.errorRaised());
        if (roundEnv.errorRaised()) {
            return true;
        }
        print("roundEnvironment.processingOver(): " + roundEnv.processingOver());
        try {
            if (classBuilder == null) {
                classBuilder = new ClassBuilder(roundEnv);
            }
            classBuilder.builder(mFiler);
        } catch (Exception e) {
            return true;
        }
        return false;
    }


    public static void error(String msg) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    public static void print(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

}