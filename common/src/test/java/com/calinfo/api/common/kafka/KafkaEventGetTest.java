package com.calinfo.api.common.kafka;

import com.calinfo.api.common.kafka.mock.KafkaService;
import com.calinfo.api.common.kafka.mock.Receiver;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.rule.KafkaEmbedded;


public class KafkaEventGetTest {


    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true);

    @Autowired
    private KafkaService kafkaService;

    @LocalServerPort
    private int port;

    @Test
    public void callOkWithoutException() {

        KafkaEvent kafkaEvent = new KafkaEvent();
        kafkaEvent.setResultException(false);

        KafkaObject kafkaObject = new KafkaObject();
        kafkaEvent.setResult(kafkaObject);
        kafkaObject.setFullQualifiedClassName(String.class.getName());
        kafkaObject.setValue("AA");


        try {
            String cible = kafkaEvent.get();
            Assert.assertEquals("AA", cible);
        } catch (Exception e) {
            Assert.fail("Il ne devrait pas y avoir d'exception de levée");
        }
    }

    @Test
    public void callOkException() {

        KafkaEvent kafkaEvent = new KafkaEvent();
        kafkaEvent.setResultException(true);

        KafkaObject kafkaObject = new KafkaObject();
        kafkaEvent.setResult(kafkaObject);
        kafkaObject.setFullQualifiedClassName(NullPointerException.class.getName());

        try {
            kafkaEvent.get();

            Assert.fail("Il devrait avoir une exception de levée");

        } catch (KafkaException e) {

            if (!(e.getCause() instanceof NullPointerException)){
                Assert.fail("Le type d'exception devrait être un NullPointerException");
            }

        }
    }


}
