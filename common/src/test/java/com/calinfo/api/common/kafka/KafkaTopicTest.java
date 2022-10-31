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
        Assert.assertEquals(kafkaEvent.getTopic(), "topicB");
        Assert.assertEquals(KafkaSubServiceImpl.class.getName(), kafkaEvent.getMetadataService().getClassType());
        Assert.assertEquals(kafkaEvent.getMetadataService().getMethodName(), "topicAWithoutException");
        Assert.assertTrue(false == kafkaEvent.getData().isReturnValueException());

        Assert.assertTrue(kafkaEvent.getData().getSerializedParametersValues().size() == 2);
        int indexParam = 0;
        Assert.assertEquals(String.class.getName(), kafkaEvent.getMetadataService().getParametersTypes().get(indexParam));
        Assert.assertEquals(kafkaEvent.getData().getSerializedParametersValues().get(indexParam), "\"A12\"");
        Assert.assertEquals(kafkaEvent.getValues().getParameterValueAt(indexParam, String.class), "A12");


        indexParam = 1;
        Assert.assertEquals(TestResource.class.getName(), kafkaEvent.getMetadataService().getParametersTypes().get(indexParam));
        Assert.assertEquals(kafkaEvent.getData().getSerializedParametersValues().get(indexParam), "{\"prop1\":\"123\"}");
        Assert.assertEquals(kafkaEvent.getValues().getParameterValueAt(indexParam, TestResource.class).getProp1(), "123");

        Assert.assertEquals(TestResource.class.getName(), kafkaEvent.getMetadataService().getReturnType());
        Assert.assertEquals(kafkaEvent.getData().getSerializedReturnValue(), "{\"prop1\":\"Propppp\"}");


        Assert.assertEquals(kafkaEvent.getTopic(), "topicB");
        Assert.assertEquals(kafkaEvent.getTopic(), "topicB");
        Assert.assertEquals(kafkaEvent.getTopic(), "topicB");

        Assert.assertEquals(kafkaEvent.getValues().getReturnValue(TestResource.class).getProp1(), "Propppp");
    }

}
