package com.calinfo.api.common.teavm;

import com.calinfo.api.common.teavm.annotations.JsClass;
import com.calinfo.api.common.teavm.annotations.JsMethod;

@JsClass(namespace = "MaClass")
public class JsClassTest {

    @JsMethod(name = "void")
    public double[] methodVoid(){
        return new double[0];
    }

    @JsMethod(name = "int")
    public float methodInt(int val){
        return 0.0f;
    }

    @JsMethod(name = "tabDouble")
    public void methodTabDouble(double[] val){
    }

    @JsMethod(name = "string")
    public String methodString(String val){
        return "";
    }
}
