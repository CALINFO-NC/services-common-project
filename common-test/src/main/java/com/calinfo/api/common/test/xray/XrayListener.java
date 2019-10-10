package com.calinfo.api.common.test.xray;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XrayListener implements IInvokedMethodListener  {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

        if (method.isConfigurationMethod()){
            testResult.setAttribute("labels", "TestNgConfigMethod");
        }

        List<String> labels = new ArrayList<>();
        labels.add("TestNg");

        if(method.isTestMethod()) {
            labels.add("TestNgTestMethod");
        }

        if(annotationPresent(method, Xray.class) ) {

            labels.add("TestNgXrayAnnoted");
            labels.addAll(Arrays.asList(method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Xray.class).labels()));

            // Pour le moment on ne met que le premier requirements car passer un tableau séparrer par des espace (comme indiqué ds la doc) ne marche pas dans xray. Un ticket a été ouvert chez l'éditeur
            String[] requirement = method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Xray.class).requirement();
            if (requirement.length > 0) {
                testResult.setAttribute("requirement", String.join(" ", requirement[0]));
            }

            testResult.setAttribute("test", method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Xray.class).test());
        }

        testResult.setAttribute("labels", String.join(" ", labels.toArray(new String[labels.size()])));
    }


    private boolean annotationPresent(IInvokedMethod method, Class clazz) {
        return method.getTestMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(clazz);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // Aucun traitement à effectuer après l'invocation
    }


}