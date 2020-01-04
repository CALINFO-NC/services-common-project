package com.calinfo.api.common.kafka;

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

import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaTopicDefinitionFactory {

    // Map<applicationId, Map<topicKey, KafkaTopicDefinition>>
    private static Map<String, Map<String, KafkaTopicDefinition>> cache = new HashMap<>();

    public static Map<String, KafkaTopicDefinition> getDefinition(String applicationId) throws IOException {

        Map<String, KafkaTopicDefinition> result = cache.computeIfAbsent(applicationId, key -> new HashMap<>());

        if (result.isEmpty()){

            try(InputStream in = KafkaTopicDefinitionFactory.class.getClassLoader().getResourceAsStream("calinfo-common-kafka-topic-definitions.json")){


                ObjectMapper mapper = new ObjectMapper();
                JavaType type = mapper.getTypeFactory().constructParametricType(Map.class, String.class, KafkaTopicDefinition.class);
                Map<String, KafkaTopicDefinition> def = mapper.readValue(in, type);

                for (Map.Entry<String, KafkaTopicDefinition> item: def.entrySet()){

                    String newKey = MiscUtils.getTopicFullName(applicationId, item.getValue().getTopicName(), item.getValue().isPrefixTopicNameWithApplicationId(), item.getValue().isPrefixTopicNameWithDomain());
                    result.put(newKey, item.getValue());
                }
            }
        }

        return result;
    }

}
