package com.calinfo.api.common.converter;

/*-
 * #%L
 * common
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

/**
 * Created by dalexis on 19/05/2018.
 */
public interface ClassConverter extends Converter{

    /**
     * 
     * @param source Source de donnée à convertir
     * @param dest Classe de destinatation dans laquel faire la convertion
     * @param contextConverter Context de convertion (peut être null). C'est le context passé à la méthode {@link com.calinfo.api.common.converter.AbstractConvertManager#convert(Object, Class, ContextConverter)} lors d'une convertion.
     *                         Si vous appelez la méthode {@link com.calinfo.api.common.converter.AbstractConvertManager#convert(Object, Class)}, alors cette valeur sera à null
     * @param <T> Type de la donnée de destionation à convertir
     * @return Donnée convertie.
     */
    <T> T convert(Object source, Class<T> dest, ContextConverter contextConverter);

}
