package com.calinfo.api.common.kafka;

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
