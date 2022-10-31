package com.calinfo.api.common.kafka.processor;

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

import com.calinfo.api.common.kafka.*;
import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auto.service.AutoService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"com.calinfo.api.common.kafka.KafkaTopic", "com.calinfo.api.common.kafka.KafkaPrefixTopic"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class KafkaTopicProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {

            LogUtils.trace("Start KafkaTopic definition generator");

            KafkaMetadataEvent kafkaMetadataEvent = new KafkaMetadataEvent();
            Map<String, KafkaMetadataTopic> topicsMetadata = kafkaMetadataEvent.getMetadataTopics();

            // On parcours les méthodes annoté par KafkaTopic,
            // Mais on les ignore si elle font partie d'une classe ne pouvant pas être
            // Instancié (comme par ex une class abstraite)
            for (Element item : roundEnv.getElementsAnnotatedWith(KafkaTopic.class)) {

                if (item instanceof ExecutableElement){
                    ExecutableElement executableElement = (ExecutableElement) item;
                    KafkaTopic ano = executableElement.getAnnotation(KafkaTopic.class);

                    TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();

                    if (!typeElement.getModifiers().contains(Modifier.ABSTRACT) &&
                            !typeElement.getModifiers().contains(Modifier.STATIC) &&
                            !typeElement.getModifiers().contains(Modifier.FINAL) &&
                            !ano.kafkaPrefixeMandatory()){
                        Pair<String, KafkaMetadataTopic> kafkaTopicDefinition = getKafkaEventDefinition(typeElement, executableElement);
                        topicsMetadata.put(kafkaTopicDefinition.getKey(), kafkaTopicDefinition.getValue());
                    }
                }
            }

            // On parcours les Classes Annoté par KafkaPrefixTopc
            for (Element item : roundEnv.getElementsAnnotatedWith(KafkaTopicPrefix.class)) {

                if (item instanceof TypeElement){
                    TypeElement typeElement = (TypeElement) item;

                    processingEnv.getElementUtils().getAllMembers(typeElement).forEach(method -> {

                        KafkaTopic kafkaTopic = method.getAnnotation(KafkaTopic.class);

                        if (kafkaTopic != null){

                            ExecutableElement executableElement = (ExecutableElement) method;

                            Pair<String, KafkaMetadataTopic> kafkaTopicDefinition = getKafkaEventDefinition(typeElement, executableElement);
                            topicsMetadata.put(kafkaTopicDefinition.getKey(), kafkaTopicDefinition.getValue());
                        }
                    });
                }
            }

            // Ajout des model
            topicsMetadata.entrySet().stream().map(e -> e.getValue().getMetadataService()).forEach(i -> initModelFromMetadataService(kafkaMetadataEvent.getMetadataModels(), i));

            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            if (!topicsMetadata.isEmpty()) {
                writeResource(mapper.writeValueAsString(kafkaMetadataEvent));
            }

        }
        catch(Exception e){
            LogUtils.error(e);
            throw new ProcessorRuntimeException(e);
        }

        // Processeur suivant peuvent traiter les annotations
        return true;

    }

    private void initModelFromMetadataService(Map<String, KafkaMetadataModel> metadataModels, KafkaMetadataService metadataService){
        KafkaUtils.splitToClassFromMetadataService(metadataService).forEach(i -> initModelFromClassType(metadataModels, i));
    }


    private void initModelFromClassType(Map<String, KafkaMetadataModel> metadataModels, String classType){

        // On ne traite pas si ca l'a déjàa été
        if (metadataModels.containsKey(classType)){
            return;
        }

        // On ne traite pas si c'est une classe Java
        if (classType.startsWith("java.")){
            return;
        }

        // On ne traite pas si ca ne vient pas d'un DTO
        TypeMirror mirrorDto = processingEnv.getElementUtils().getTypeElement(Serializable.class.getName()).asType();

        TypeElement elementClazz = processingEnv.getElementUtils().getTypeElement(classType);
        TypeMirror mirrorClazz = null;
        if (elementClazz != null) {
            mirrorClazz = elementClazz.asType();
        }
        if (mirrorClazz == null || !processingEnv.getTypeUtils().isAssignable(mirrorClazz, mirrorDto) || mirrorClazz.getKind().isPrimitive()){
            return;
        }

        KafkaMetadataModel model = new KafkaMetadataModel();
        metadataModels.put(classType, model);
        model.setClassType(mirrorClazz.toString());

        if (elementClazz.getSuperclass() != null) {
            KafkaUtils.splitToClassFromStringMirrorClass(elementClazz.getSuperclass().toString()).forEach(i -> initModelFromClassType(metadataModels, i));
            model.setSuperClassType(KafkaUtils.deleteAnnotationToStringMirror(elementClazz.getSuperclass().toString()));
        }

        initModelProperties(metadataModels, model.getProperties(), classType);
    }

    private void initModelProperties(Map<String, KafkaMetadataModel> metadataModels, Map<String, String> properties, String classType){

        TypeElement elementClazz = processingEnv.getElementUtils().getTypeElement(classType);

        elementClazz.getEnclosedElements().forEach(element -> {

            JsonIgnore ano = element.getAnnotation(JsonIgnore.class);
            if (element.getKind().isField() &&
                    !element.getModifiers().contains(Modifier.STATIC) &&
                    !element.getModifiers().contains(Modifier.FINAL) &&
                    ano == null) {
                properties.put(element.toString(), KafkaUtils.deleteAnnotationToStringMirror(element.asType().toString()));
                KafkaUtils.splitToClassFromStringMirrorClass(element.asType().toString()).forEach(i -> initModelFromClassType(metadataModels, i));
            }
        });
    }

    private Pair<String, KafkaMetadataTopic> getKafkaEventDefinition(TypeElement typeElement, ExecutableElement executableElement) {

        KafkaTopic kafkaTopic = executableElement.getAnnotation(KafkaTopic.class);
        KafkaTopicPrefix kafkaTopicPrefix = typeElement.getAnnotation(KafkaTopicPrefix.class);

        ExecutableType methodType = (ExecutableType) processingEnv.getTypeUtils().asMemberOf((DeclaredType) typeElement.asType(), executableElement);

        String key = MiscUtils.getTopicFullName(KafkaMetadataUtils.TEMPLATE_APPLICATION_ID, KafkaMetadataUtils.TEMPLATE_DOMAIN_NAME, kafkaTopic, kafkaTopicPrefix);

        KafkaMetadataTopic kafkaMetadataTopic = new KafkaMetadataTopic();
        kafkaMetadataTopic.setPrefixTopicNameWithApplicationId(kafkaTopic.prefixTopicNameWithApplicationId());
        kafkaMetadataTopic.setPrefixTopicNameWithDomain(kafkaTopic.prefixTopicNameWithDomain());
        kafkaMetadataTopic.setTopicFullKey(MiscUtils.getTopicFullKey(kafkaTopicPrefix == null ? null : kafkaTopicPrefix.value(), kafkaTopic.value()));
        KafkaMetadataService metadataService = new KafkaMetadataService();
        kafkaMetadataTopic.setMetadataService(metadataService);
        metadataService.setMethodName(executableElement.getSimpleName().toString());
        metadataService.setReturnType(KafkaUtils.deleteAnnotationToStringMirror(methodType.getReturnType().toString()));

        for (int i = 0; i < methodType.getParameterTypes().size(); i++){
            TypeMirror actualParamType = methodType.getParameterTypes().get(i);
            metadataService.getParametersTypes().put(i, KafkaUtils.deleteAnnotationToStringMirror(actualParamType.toString()));
        }

        metadataService.setClassType(KafkaUtils.deleteAnnotationToStringMirror(typeElement.asType().toString()));

        return new ImmutablePair<>(key, kafkaMetadataTopic);
    }

    private void writeResource(String value) throws IOException {

        String resourceName = "calinfo-common-kafka-topic-definitions.json";
        FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourceName);

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.openOutputStream(), "UTF-8"))){

            pw.println(value);
        }
    }
}
