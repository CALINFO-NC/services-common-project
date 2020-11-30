package com.calinfo.api.common.kafka;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.kafka.mock.KafkaService;
import com.calinfo.api.common.kafka.mock.KafkaSubServiceImpl;
import com.calinfo.api.common.kafka.mock.Receiver;
import com.calinfo.api.common.kafka.mock.TestResource;

import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class} , webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("kafka")
@EmbeddedKafka
public class KafkaTopicTest extends AbstractTestNGSpringContextTests {


    private static final long AWAIT = 1_000;

    @Autowired
    private Receiver receiver;

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
        KafkaObject kafkaObject = kafkaEvent.getParameters().get(0);
        Assert.assertEquals(String.class.getName(), kafkaObject.getFullQualifiedClassName());
        Assert.assertEquals("\"A12\"", kafkaObject.getStrValue());
        Assert.assertEquals("A12", kafkaObject.get());



        kafkaObject = kafkaEvent.getParameters().get(1);
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
