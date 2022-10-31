package com.calinfo.api.common.kafka;

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

import com.calinfo.api.common.ex.ApplicationErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaMetadataUtils {

    public static final String TEMPLATE_APPLICATION_ID = "<ApplicationId>";
    public static final String TEMPLATE_DOMAIN_NAME = "<DomainName>";

    private static KafkaMetadataEvent cache = null;

    private static KafkaMetadataEvent getCache(){

        if(cache == null){
            try (InputStream in = KafkaMetadataUtils.class.getClassLoader().getResourceAsStream("calinfo-common-kafka-topic-definitions.json")) {

                ObjectMapper mapper = new ObjectMapper();
                cache = mapper.readValue(in, KafkaMetadataEvent.class);
            }
            catch (IOException e){
                throw new ApplicationErrorException(e);
            }
        }

        return cache;
    }


    public static Map<String, KafkaMetadataTopic> getTopicMetadatas() {
        return getCache().getMetadataTopics();
    }

    public static Map<String, KafkaMetadataModel> getModelMetadatas(KafkaMetadataService metadataService) {

        List<String> lstClass = KafkaUtils.splitToClassFromMetadataService(metadataService);
        Map<String, KafkaMetadataModel> result = getCache().getMetadataModels().entrySet().stream().filter(e -> lstClass.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<String> lstSuperClass = new ArrayList<>();
        result.values().stream().forEach(i -> lstSuperClass.addAll(KafkaUtils.splitToClassFromStringMirrorClass(i.getSuperClassType())));
        result.putAll(getCache().getMetadataModels().entrySet().stream().filter(e -> lstSuperClass.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return result;
    }
}
