package com.calinfo.api.common.converter;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
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
