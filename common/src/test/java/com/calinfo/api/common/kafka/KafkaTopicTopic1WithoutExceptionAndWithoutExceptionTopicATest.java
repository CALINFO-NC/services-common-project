package com.calinfo.api.common.kafka;

import com.calinfo.api.common.kafka.mock.KafkaService;
import com.calinfo.api.common.kafka.mock.Receiver;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("kafka")
public class KafkaTopicTopic1WithoutExceptionAndWithoutExceptionTopicATest {


    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true);

    @Autowired
    private KafkaService kafkaService;

    @Test
    public void call() throws Exception {

        receiver.clearKafkaEvent();

        kafkaService.topic1WithoutExceptionAndWithoutExceptionTopicA();
        KafkaEvent kafkaEventTopic1 = receiver.getKafkaEventTopic1();
        KafkaEvent kafkaEventTopicA = receiver.getKafkaEventTopicA();

        Assert.assertNotNull(kafkaEventTopic1);
        Assert.assertNotNull(kafkaEventTopicA);
    }


}
