package com.calinfo.api.common.test.xray;

/*-
 * #%L
 * common-test
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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
