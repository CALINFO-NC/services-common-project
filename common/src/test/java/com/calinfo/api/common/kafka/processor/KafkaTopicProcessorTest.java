package com.calinfo.api.common.kafka.processor;

import com.calinfo.api.common.kafka.KafkaTopicDefinition;
import com.calinfo.api.common.kafka.KafkaTopicDefinitionFactory;
import com.calinfo.api.common.utils.MiscUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class KafkaTopicProcessorTest {

    @Autowired
    private KafkaTopicDefinitionFactory kafkaTopicDefinitionFactory;

    @Test
    public void call() throws IOException {

        // On vérifie que ce fichier existe
        File file = new File(getClass().getClassLoader().getResource("calinfo-common-kafka-topic-definitions.json").getFile());

        Assert.assertNotNull(file);
        Assert.assertNotNull(file.exists());

        // On vérifie que l'application nous renvoie bien les définition
        String applicationId = "arbitraire";
        Map<String, KafkaTopicDefinition> def = KafkaTopicDefinitionFactory.getDefinition(applicationId);
        Assert.assertFalse(def.isEmpty());
        Assert.assertNotNull(def.get(MiscUtils.getTopicFullName(applicationId, "topic0", true)));

    }
}
