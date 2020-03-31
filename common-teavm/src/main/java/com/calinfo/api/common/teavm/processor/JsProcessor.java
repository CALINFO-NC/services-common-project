package com.calinfo.api.common.teavm.processor;

/*-
 * #%L
 * common-teavm
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.calinfo.api.common.teavm.annotations.JsClass;
import com.calinfo.api.common.teavm.annotations.JsMethod;
import com.calinfo.api.common.teavm.metadata.SignatureJvm;
import com.calinfo.api.common.teavm.utils.LogUtils;
import com.google.auto.service.AutoService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"com.calinfo.api.common.teavm.annotations.JsClass"})
@AutoService(Processor.class)
public class JsProcessor extends AbstractProcessor {

    private List<SignatureJvm> cache = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            LogUtils.trace("Start js class generation");

            for (Element jsClassElement : roundEnv.getRootElements()) {
                analyzeJsClassElement(jsClassElement);
            }

            generateTeaVmClass();

            LogUtils.trace("End js class generation");
        }
        catch(Exception e){
            LogUtils.error(e);
            throw new ProcessorRuntimeException(e);
        }

        // Processeur suivant peuvent traiter les annotations
        return true;
    }

    private void analyzeJsClassElement(Element jsClassElement){

        LogUtils.trace(String.format("Traitement de l'élément : '%s' ", jsClassElement.toString()));

        if (!(jsClassElement instanceof TypeElement)) {
            LogUtils.trace("Element ignoré car ce n'est pas une classe");
            return;
        }

        TypeElement typeElement = (TypeElement)jsClassElement;
        JsClass jsClass = jsClassElement.getAnnotation(JsClass.class);

        if (jsClass == null) {
            LogUtils.trace("Element ignoré car il n'est pas annoté.");
            return;
        }

        for (Element jsMethodElement : typeElement.getEnclosedElements()){
            analyzeJsMethodElement(typeElement, jsClass, jsMethodElement);
        }

    }

    private void analyzeJsMethodElement(TypeElement jsClassElement, JsClass jsClass, Element jsMethodElement){

        LogUtils.trace(String.format("Traitement de l'élément : '%s.%s' ", jsClassElement.toString(), jsMethodElement.toString()));

        if (!(jsMethodElement instanceof ExecutableElement)) {
            LogUtils.trace("Element ignoré car ce n'est pas une méthode");
            return;
        }

        ExecutableElement execElement = (ExecutableElement)jsMethodElement;
        JsMethod jsMethod = execElement.getAnnotation(JsMethod.class);

        if (jsMethod == null) {
            LogUtils.trace("Element ignoré car il n'est pas annoté.");
            return;
        }

        if (jsClassElement.getSimpleName().toString().equals(jsMethodElement.toString()) && !execElement.getParameters().isEmpty()){
            throw new ProcessorRuntimeException(String.format("La classe '%s' annoté @%s n'est pas autorisée à avoir des paramètres dans le constructeur", jsClassElement.toString(), JsClass.class.getName()));
        }

        SignatureJvm signatureJvm = getMetadata(jsClassElement, execElement, jsClass, jsMethod);

        if (cache.contains(signatureJvm)){
            throw new ProcessorRuntimeException(String.format("Il ne peut pas avoir deux couples {Espace de nommage/Nom de méthode} indentique : '%s'", signatureJvm.getNameSpaceAndMethodName()));
        }

        if (!execElement.getThrownTypes().isEmpty()){
            throw new ProcessorRuntimeException(String.format("La méthode '%s.%s' annoté '%s' n'est pas autorisée à catcher des esceptions. Cependant les exceptions de type Runtime (ou étendue) génère une exception javascript.", jsClassElement.toString(), jsMethodElement.toString(), JsClass.class.getName()));
        }

        cache.add(signatureJvm);
    }

    private SignatureJvm getMetadata(TypeElement typeElement, ExecutableElement executableElement, JsClass jsClass, JsMethod jsMethod){

        SignatureJvm signatureJvm = new SignatureJvm();

        signatureJvm.setNameSpaceAndMethodName(String.join(".", jsClass.namespace(), jsMethod.name()));
        signatureJvm.setReturnType(getStrType(executableElement.getReturnType()));
        signatureJvm.setClassAndMethodName(String.join(".", typeElement.getQualifiedName().toString(), executableElement.getSimpleName().toString()));

        executableElement.getParameters().stream().forEach(variableElement -> signatureJvm.getParameters().add(getStrType(variableElement.asType())));

        LogUtils.trace(String.format("Méthode Java retranscrite en javascript : '%s(...)' -> '%s'", signatureJvm.getNameSpaceAndMethodName(), signatureJvm.toString()));

        return signatureJvm;
    }

    private String getStrType(TypeMirror typeMirror){

        String result = null;

        switch (typeMirror.getKind()) {

            case BOOLEAN:
                result = boolean.class.getName();
                break;

            case BYTE:
                result = byte.class.getName();
                break;

            case SHORT:
                result = short.class.getName();
                break;

            case INT:
                result = int.class.getName();
                break;

            case FLOAT:
                result = float.class.getName();
                break;

            case DOUBLE:
                result = double.class.getName();
                break;

            case VOID:
                result = void.class.getName();
                break;

            case ARRAY:
                ArrayType arrayType = (ArrayType) typeMirror;
                result = String.format("%s[]", getStrType(arrayType.getComponentType()));
                break;

            default:
                if (!typeMirror.getKind().isPrimitive()) {
                    Types typeUtils = processingEnv.getTypeUtils();
                    TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
                    result = typeElement.toString();
                }
        }

        return result;
    }

    private String getJvmType(String strType){

        if (strType.equals(boolean.class.getName())){
            return "Z";
        }

        if (strType.equals(byte.class.getName())){
            return "B";
        }

        if (strType.equals(short.class.getName())){
            return "S";
        }

        if (strType.equals(int.class.getName())){
            return "I";
        }

        if (strType.equals(float.class.getName())){
            return "F";
        }

        if (strType.equals(double.class.getName())){
            return "D";
        }

        if (strType.equals(void.class.getName())){
            return "V";
        }

        if (strType.endsWith("[]")){
            return String.format("[%s", getJvmType(strType.substring(0, strType.length() - 2)));
        }

        return String.format("L%s;", strType.replaceAll("\\.", "/"));
    }

    private boolean isClass(String strType){

        return !strType.equals(boolean.class.getName()) &&
                !strType.equals(byte.class.getName()) &&
                !strType.equals(short.class.getName()) &&
                !strType.equals(int.class.getName()) &&
                !strType.equals(float.class.getName()) &&
                !strType.equals(double.class.getName()) &&
                !strType.equals(void.class.getName());


    }

    private boolean isArray(String strType){
        return strType.endsWith("[]");
    }

    private String getItemTypeArray(String strType){
        return strType.substring(0, strType.length() - 2);
    }

    private void generateTeaVmClass() throws IOException {

        String packageName = System.getProperty("calinfo.common.teavm.main.packageName", "com.calinfo.teavm");
        String className = System.getProperty("calinfo.common.teavm.main.className", "JsClass");

        List<String> lstJavaMethodNameInClass = new ArrayList<>();

        String sourceFilePkg = String.join(".", packageName, className);
        JavaFileObject builderFile;
        try {
            builderFile = processingEnv.getFiler().createSourceFile(sourceFilePkg);
        }
        catch(FilerException e){
            LogUtils.trace(e.getMessage());
            return;
        }

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            out.println(MessageFormat.format("package {0};", packageName));
            out.println();
            writeImport(out);
            out.println();

            out.println(String.format("public class %s {", className));

            // Ecritures des méthodes Java Binder en Javascript
            cache.stream().forEach(signatureJvm -> lstJavaMethodNameInClass.add(writeJsBodyAndGetJavaMethodeName(out, signatureJvm)));
            out.println();

            // Ecriture de la méthode main
            out.println("public static void main(String[] args) {");
            lstJavaMethodNameInClass.stream().forEach(item -> out.println(String.format("%s();", item)));
            out.println("}");

            // Fermeture de la classe
            out.println("}");

        }
    }

    private String writeJsBodyAndGetJavaMethodeName(PrintWriter out, SignatureJvm signatureJvm){

        // Création du name space javascript
        StringBuilder javaMethodName = new StringBuilder("js");
        StringBuilder namespace = new StringBuilder(System.getProperty("calinfo.common.teavm.main.rootNameSpace", "document.$server"));
        StringBuilder scripts = new StringBuilder(String.format("if (%s === null || %s === undefined) { %s = {}; } ", namespace, namespace, namespace));
        Arrays.stream(signatureJvm.getNameSpaceAndMethodName().split("\\.")).forEach(item -> {
            scripts.append(String.format("if (%s.%s === null || %s.%s === undefined) { %s.%s = {}; } ", namespace, item, namespace, item, namespace, item));
            namespace.append(".").append(item);
            javaMethodName.append(StringUtils.capitalize(item));
        });

        // Création des paramètres javascript
        List<String> lstPrm = new ArrayList<>();
        for (int i = 0; i < signatureJvm.getParameters().size(); i++){
            lstPrm.add(String.format("prm%s", i));
        }
        String strPrm = String.join(", ", lstPrm.toArray(new String[lstPrm.size()]));

        // Construction de la fonction javascript
        String returnStr = "";
        if (!signatureJvm.getReturnType().equals(void.class.getName())){
            returnStr = "return";
        }
        scripts.append(String.format("%s = function(%s) { %s javaMethods.get('%s').invoke(%s); }; ", namespace, strPrm, returnStr, getJvmSignature(signatureJvm), strPrm));

        // Ecriture de la méthode java
        out.println(String.format("@JSBody(script = \"%s\")", scripts));
        out.println(String.format("static native void %s();", javaMethodName));

        return javaMethodName.toString();
    }

    private String getJvmSignature(SignatureJvm signatureJvm){

        List<String> prmJvm = signatureJvm.getParameters().stream().map(this::getJvmType).collect(Collectors.toList());

        return MessageFormat.format("{0}({1}){2})", signatureJvm.getClassAndMethodName(), String.join("", prmJvm.toArray(new String[prmJvm.size()])), getJvmType(signatureJvm.getReturnType()));
    }

    private void writeImport(PrintWriter out){

        cache.stream().forEach(signatureJvm -> {

            String strType = signatureJvm.getReturnType();
            if (isArray(strType)){
                strType = getItemTypeArray(strType);
            }

            if (signatureJvm.getReturnType() != null && isClass(strType)){
                out.println(MessageFormat.format("import {0};", signatureJvm.getReturnType()));
            }

            signatureJvm.getParameters().stream().forEach(parameter -> {

                String strTypePrm = parameter;
                if (isArray(strTypePrm)){
                    strTypePrm = getItemTypeArray(strTypePrm);
                }

                if (isClass(strTypePrm)){
                    out.println(MessageFormat.format("import {0};", strTypePrm));
                }
            });

        });

        out.println("import org.teavm.jso.JSBody;");
    }

}
