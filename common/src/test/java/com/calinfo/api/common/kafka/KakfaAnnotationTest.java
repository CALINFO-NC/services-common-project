package com.calinfo.api.common.kafka;


import com.calinfo.api.common.resource.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
@EnableSpringDataWebSupport
@ActiveProfiles("kafka")
public class KakfaAnnotationTest {

    @Autowired
    private KafkaEmbedded kafkaEmbedded;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private Consumer<String, KakfaRequestMessage<TestResource>> consumer;

    private ObjectMapper objectMapper;

    DefaultKafkaConsumerFactory<String, KakfaRequestMessage<TestResource>> defaultKafkaConsumerFactory;

    @Before
    public void before() throws Exception {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false", kafkaEmbedded);

        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.calinfo.api.common.kafka");

        defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps);

        consumer = defaultKafkaConsumerFactory.createConsumer();
        kafkaEmbedded.consumeFromAllEmbeddedTopics(consumer);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAopPublish() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new TestResource("test")))
        );

        ConsumerRecords<String, KakfaRequestMessage<TestResource>> records = KafkaTestUtils.getRecords(consumer);

        Assert.assertEquals(1, records.count());

        TestResource result = objectMapper.convertValue(records.iterator().next().value().getResult(), TestResource.class);

        Assert.assertEquals("test", result.getName());

        consumer.close();

    }


    @Test
    public void testAopPublishError() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/api/v1/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new TestResource("test")))
        );

        ConsumerRecords<String, KakfaRequestMessage<TestResource>> records = KafkaTestUtils.getRecords(consumer);

        Assert.assertEquals(1, records.count());

        KafkaErrorMessage result = objectMapper.convertValue(records.iterator().next().value().getResult(), KafkaErrorMessage.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());

        consumer.close();

    }



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
}
