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

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Classe permettant de gérer des converter.
 * Prennez soins d'ajouter {@link org.springframework.stereotype.Component} sur la classe fille
 */
public abstract class AbstractConvertManager {

    //Structure de la map : Map<Source, Map<Dest, Converter>>
    private Map<Class<?>, Map<Class<?>, Converter>> cache = new HashMap<>();

    private List<Converter> converterStore = new ArrayList<>();

    private static ReentrantLock locker = new ReentrantLock();


    /**
     * Méthode abstrait permettant d'initialiser ce converter.
     * Prennez soint d'ajouter {@link javax.annotation.PostConstruct} sur cette méthode
     */
    public abstract void postConstruct();

    protected void addConverter(InstanceConverter converter){
        converterStore.add(converter);
    }

    protected void addConverter(ClassConverter converter){
        converterStore.add(converter);
    }

    public <T> T convert (@NotNull Object source, @NotNull T dest){
        return convert(source, dest, null);
    }

    public <T> T convert (@NotNull Object source, @NotNull T dest, ContextConverter contextConverter){

        if (dest == source)
            return dest;

        Converter converter = findConverter(source.getClass(), source.getClass(), dest.getClass(), contextConverter);

        if (converter == null) {
            return dest;
        }

        if (!(converter instanceof InstanceConverter)){
            throw new ConverterNotFoundException(String.format("No instance converter to convert '%s' to '%s'", source.getClass().getName(), dest.getClass().getName()));
        }

        return ((InstanceConverter)converter).convert(source, dest, contextConverter);
    }

    public <T> T convert (Object source, @NotNull Class<T> dest){
        return convert(source, dest, null);
    }

    public <T> T convert (Object source, @NotNull Class<T> dest, ContextConverter contextConverter){

        if (source == null){
            return null;
        }

        Converter converter = findConverter(source.getClass(), source.getClass(), dest, contextConverter);

        if (converter == null) {
            throw new ConverterNotFoundException(String.format("No converter to convert '%s' to '%s'", source.getClass().getName(), dest.getName()));
        }

        if (converter instanceof InstanceConverter){

            // Tentative de créer une instance de a class dest
            try {

                T t = dest.getDeclaredConstructor().newInstance();
                return ((InstanceConverter)converter).convert(source, t, contextConverter);

            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new ConverterNotFoundException(String.format("No class converter to convert '%s' to '%s'", source.getClass().getName(), dest.getClass().getName()));
            }
        }

        return ((ClassConverter)converter).convert(source, dest, contextConverter);
    }

    private Converter findConverter(Class<?> sourceRoot, Class<?> source, Class<?> dest, ContextConverter contextConverter){

        if (source == null && dest == null) {
            return null;
        }

        if (source == null) {
            return findConverter(sourceRoot, sourceRoot, dest.getSuperclass(), contextConverter);
        }

        if (source.equals(sourceRoot) && dest == null){
            return null;
        }

        locker.lock();
        Map<Class<?>, Converter> map;
        try {
            map = cache.computeIfAbsent(source, k -> new HashMap<>());
        }
        finally {
            locker.unlock();
        }

        if (map.containsKey(dest))
            return map.get(dest);

        for (Converter item : converterStore){

            if (item.accept(source, dest, contextConverter)) {
                map.put(dest, item);
                return item;
            }
        }

        return findConverter(sourceRoot, source.getSuperclass(), dest, contextConverter);
    }

    public <A, B> Function<A, B> toFunction (@NotNull Class<B> classB){
        return new FunctionConverter<>(this, classB);
    }
}
