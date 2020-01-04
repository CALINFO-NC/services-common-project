package com.calinfo.api.common.kafka.processor;

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

import com.calinfo.api.common.kafka.KafkaTopic;
import com.calinfo.api.common.kafka.KafkaTopicDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auto.service.AutoService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"com.calinfo.api.common.kafka.KafkaTopic"})
@AutoService(Processor.class)
public class KafkaTopicProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        try {

            LogUtils.trace("Start KafkaTopic definition generator");

            Map<String, KafkaTopicDefinition> definition = new HashMap<>();

            for (Element item : roundEnv.getElementsAnnotatedWith(KafkaTopic.class)) {

                ExecutableElement executableElement = (ExecutableElement) item;
                Pair<String, KafkaTopicDefinition> kafkaTopicDefinition = getKafkaEventDefinition(executableElement);
                definition.put(kafkaTopicDefinition.getKey(), kafkaTopicDefinition.getValue());
            }

            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            if (!definition.isEmpty()) {
                writeResource(mapper.writeValueAsString(definition));
            }

        }
        catch(Exception e){
            LogUtils.error(e);
            throw new ProcessorRuntimeException(e);
        }

        // Processeur suivant peuvent traiter les annotations
        return true;

    }

    private Pair<String, KafkaTopicDefinition> getKafkaEventDefinition(ExecutableElement executableElement) {

        KafkaTopic kafkaTopic = executableElement.getAnnotation(KafkaTopic.class);

        String key = kafkaTopic.value();

        KafkaTopicDefinition kafkaTopicDefinition = new KafkaTopicDefinition();
        kafkaTopicDefinition.setPrefixTopicNameWithApplicationId(kafkaTopic.prefixTopicNameWithApplicationId());
        kafkaTopicDefinition.setPrefixTopicNameWithDomain(kafkaTopic.prefixTopicNameWithDomain());
        kafkaTopicDefinition.setTopicName(kafkaTopic.value());
        kafkaTopicDefinition.setMethodServiceName(executableElement.getSimpleName().toString());
        kafkaTopicDefinition.setReturnType(executableElement.getReturnType().toString());

        for(VariableElement param : executableElement.getParameters()){
            kafkaTopicDefinition.getParametersType().add(param.asType().toString());
        }

        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        kafkaTopicDefinition.setFullQualifiedServiceClassName(typeElement.getQualifiedName().toString());

        return new ImmutablePair<>(key, kafkaTopicDefinition);
    }

    private void writeResource(String value) throws IOException {

        String resourceName = "calinfo-common-kafka-topic-definitions.json";
        FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourceName);

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.openOutputStream(), "UTF-8"))){

            pw.println(value);
        }
    }
}
