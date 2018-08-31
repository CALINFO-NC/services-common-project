package com.calinfo.api.common.kafka;


import com.calinfo.api.common.resource.Resource;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KakfaAnnotationTest {

    private KafkaEmbedded embeddedKafka = KafkaProducerConfig.getEmbeddedKafka();

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KakfaTestController kakfaTestController;

    @Autowired
    private TopicsControllers topicsControllers;

    static class TestResource extends Resource {

    }

    @Test
    public void testAopPublish() throws Exception {

        kakfaTestController.create(new TestResource());


        Assert.assertEquals(AdminClient.create(kafkaAdmin.getConfig()).listTopics().names().get(), topicsControllers.listTopics());


    }

}
