package com.calinfo.api.common.converter;

import java.util.function.Function;

/**
 * Fonction permettant de transformer une classe en une autre en utilisant le convert manager
 */
class FunctionConverter<A, B> implements Function<A, B> {

    private AbstractConvertManager cm;
    private Class<B> classB;

    public FunctionConverter(AbstractConvertManager cm, Class<B> classB){
        this.cm = cm;
        this.classB = classB;
    }

    @Override
    public B apply(A a) {
        return cm.convert(a, classB);
    }
}
