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
public class KafkaTopicTopic1WithExceptionAfterCallTopicATest {


    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1);

    @Autowired
    private KafkaService kafkaService;

    @Test
    public void call() throws Exception {

        receiver.clearKafkaEvent();

        try {
            kafkaService.topic1WithExceptionAfterCallTopicA();
            Assert.fail("Il devrait y avoir une exception");
        }
        catch(Exception e){
            // On ne traite pas l'exception
        }
        KafkaEvent kafkaEventTopic1 = receiver.getKafkaEventTopic1();
        KafkaEvent kafkaEventTopicA = receiver.getKafkaEventTopicA();

        Assert.assertNull(kafkaEventTopic1);
        Assert.assertNull(kafkaEventTopicA);
    }

}
