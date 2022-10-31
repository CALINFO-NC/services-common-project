package com.calinfo.api.common.kafka.processor;

import com.calinfo.api.common.kafka.KafkaMetadataTopic;
import com.calinfo.api.common.kafka.KafkaMetadataUtils;
import com.calinfo.api.common.utils.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class KafkaTopicProcessorTest {

    @Autowired
    private KafkaMetadataUtils kafkaMetadataUtils;

    @Test
    public void call() throws IOException {

        // On vérifie que ce fichier existe
        File file = new File(getClass().getClassLoader().getResource("calinfo-common-kafka-topic-definitions.json").getFile());

        Assert.assertNotNull(file);
        Assert.assertNotNull(file.exists());

        // On vérifie que l'application nous renvoie bien les définition
        String applicationId = "arbitraire";
        String domainName = "arbitraireDomain";
        Map<String, KafkaMetadataTopic> def = KafkaMetadataUtils.getTopicMetadatas();
        Assert.assertFalse(def.isEmpty());
        Assert.assertNotNull(def.get(MiscUtils.getTopicFullName(KafkaMetadataUtils.TEMPLATE_APPLICATION_ID, KafkaMetadataUtils.TEMPLATE_DOMAIN_NAME, "Pre.topic0", true, true)));

    }
}
