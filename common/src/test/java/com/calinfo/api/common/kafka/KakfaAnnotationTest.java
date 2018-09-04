package com.calinfo.api.common.kafka;


import com.calinfo.api.common.kafka.mock.KakfaTestController;
import com.calinfo.api.common.resource.Resource;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
@ActiveProfiles("kafka")
public class KakfaAnnotationTest {


    @Autowired
    private KafkaEmbedded kafkaEmbedded;


    @Autowired
    private KakfaTestController kakfaTestController;


    public static class TestResource extends Resource {

        private String name;

        public TestResource() {

        }

        public TestResource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testAopPublish() throws Exception {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false", kafkaEmbedded);

        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.calinfo.api.common.kafka");

        DefaultKafkaConsumerFactory<String, TestResource> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps);

        Consumer<String, TestResource> consumer = cf.createConsumer();

        kafkaEmbedded.consumeFromAllEmbeddedTopics(consumer);

        TestResource testResource = new TestResource("testResource");

        kakfaTestController.create(testResource);

        ConsumerRecords<String, TestResource> records = KafkaTestUtils.getRecords(consumer);

        Assert.assertEquals(1, records.count());
        Assert.assertEquals("testResource", records.iterator().next().value().name);

    }

}
