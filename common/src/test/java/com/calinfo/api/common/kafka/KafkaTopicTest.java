package com.calinfo.api.common.kafka;

import com.calinfo.api.common.kafka.mock.KafkaService;
import com.calinfo.api.common.kafka.mock.KafkaSubServiceImpl;
import com.calinfo.api.common.kafka.mock.Receiver;
import com.calinfo.api.common.kafka.mock.TestResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("kafka")
public class KafkaTopicTest {


    private static final long AWAIT = 1_000;

    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true);

    @Autowired
    private KafkaService kafkaService;

    private CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void call() throws Exception {

        try {
            kafkaService.topic4WithExceptionAfterCallTopicA();
            Assert.fail("Il devrait y avoir une exception");
        }
        catch(Exception e){
            // On ne traite pas l'exception
        }
        lock.await(AWAIT, TimeUnit.MILLISECONDS);
        Assert.assertTrue(receiver.getLstKafkaEvent().isEmpty());

        ///////////////////////////////////////////////////////////////
        try {
            kafkaService.topic3WithExceptionBeforeCallTopicA();
            Assert.fail("Il devrait y avoir une exception");
        }
        catch(Exception e){
            // On ne traite pas l'exception
        }
        lock.await(AWAIT, TimeUnit.MILLISECONDS);
        Assert.assertTrue(receiver.getLstKafkaEvent().isEmpty());

        ///////////////////////////////////////////////////////////////

        try {
            kafkaService.topic2WithoutExceptionAndWithExceptionTopicB();
            Assert.fail("Il devrait y avoir une exception");
        }
        catch(Exception e){
            // On ne traite pas l'exception
        }
        lock.await(AWAIT, TimeUnit.MILLISECONDS);
        Assert.assertTrue(receiver.getLstKafkaEvent().isEmpty());

        ///////////////////////////////////////////////////////////////
        kafkaService.topic1WithoutExceptionAndWithoutExceptionTopicA();

        lock.await(AWAIT, TimeUnit.MILLISECONDS);
        Assert.assertTrue(receiver.getLstKafkaEvent().size() > 0);


        ///////////////////////////////////////////////////////////////
        KafkaEvent kafkaEvent = receiver.getLstKafkaEvent().get(0);

        Assert.assertNotNull(kafkaEvent);
        Assert.assertEquals("topicB", kafkaEvent.getTopic());
        Assert.assertEquals(KafkaSubServiceImpl.class.getName(), kafkaEvent.getFullQualifiedServiceClassName());
        Assert.assertEquals("topicAWithoutException", kafkaEvent.getMethodServiceName());
        Assert.assertTrue(false == kafkaEvent.isResultException());

        Assert.assertTrue(kafkaEvent.getParameters().size() == 2);
        Iterator<KafkaObject> it = kafkaEvent.getParameters().iterator();
        KafkaObject kafkaObject = it.next();
        Assert.assertEquals(String.class.getName(), kafkaObject.getFullQualifiedClassName());
        Assert.assertEquals("\"A12\"", kafkaObject.getStrValue());
        Assert.assertEquals("A12", kafkaObject.get());



        kafkaObject = it.next();
        Assert.assertEquals(TestResource.class.getName(), kafkaObject.getFullQualifiedClassName());
        Assert.assertEquals("{\"prop1\":\"123\"}", kafkaObject.getStrValue());
        TestResource resRes = kafkaObject.get();
        Assert.assertEquals("123", resRes.getProp1());


        Assert.assertEquals(TestResource.class.getName(), kafkaEvent.getResult().getFullQualifiedClassName());
        Assert.assertEquals("{\"prop1\":\"Propppp\"}", kafkaEvent.getResult().getStrValue());


        Assert.assertEquals("topicB", kafkaEvent.getTopic());
        Assert.assertEquals("topicB", kafkaEvent.getTopic());
        Assert.assertEquals("topicB", kafkaEvent.getTopic());

        resRes = kafkaEvent.get();
        Assert.assertEquals("Propppp", resRes.getProp1());
    }

}
