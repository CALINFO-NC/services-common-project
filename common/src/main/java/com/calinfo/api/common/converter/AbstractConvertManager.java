package com.calinfo.api.common.converter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Classe permettant de gérer des converter.
 * Prennez soins d'ajouter {@link org.springframework.stereotype.Component} sur la classe fille
 */
public abstract class AbstractConvertManager {

    //Structure de la map : Map<Source, Map<Dest, Converter>>
    private Map<Class<?>, Map<Class<?>, Converter>> cache = new HashMap<>();

    private List<Converter> converterStore = new ArrayList<>();


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

        if (dest == source)
            return dest;

        Converter converter = findConverter(source.getClass(), source.getClass(), dest.getClass());

        if (converter == null) {
            return dest;
        }

        if (!(converter instanceof InstanceConverter)){
            throw new ConverterNotFoundException(String.format("No instance converter to convert '%s' to '%s'", source.getClass().getName(), dest.getClass().getName()));
        }

        return ((InstanceConverter)converter).convert(source, dest);
    }

    public <T> T convert (@NotNull Object source, @NotNull Class<T> dest){

        Converter converter = findConverter(source.getClass(), source.getClass(), dest);

        if (converter == null) {
            throw new ConverterNotFoundException(String.format("No converter to convert '%s' to '%s'", source.getClass().getName(), dest.getName()));
        }

        if (converter instanceof InstanceConverter){

            // Tentative de créer une instance de a class dest
            try {

                T t = dest.newInstance();
                return ((InstanceConverter)converter).convert(source, t);

            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConverterNotFoundException(String.format("No class converter to convert '%s' to '%s'", source.getClass().getName(), dest.getClass().getName()));
            }
        }

        return ((ClassConverter)converter).convert(source, dest);
    }

    private Converter findConverter(Class<?> sourceRoot, Class<?> source, Class<?> dest){

        if (source == null && dest == null)
            return null;

        if (source == null)
            return findConverter(sourceRoot, sourceRoot, dest.getSuperclass());

        Map<Class<?>, Converter> map = cache.computeIfAbsent(source, k -> new HashMap<>());
        if (map.containsKey(dest))
            return map.get(dest);

        for (Converter item : converterStore){

            if (item.accept(source, dest)) {
                map.put(dest, item);
                return item;
            }
        }

        return findConverter(sourceRoot, source.getSuperclass(), dest);
    }

    public <A, B> Function<A, B> toFunction (@NotNull Class<B> classB){
        return new FunctionConverter<>(this, classB);
    }
}
