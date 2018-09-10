package com.calinfo.api.common.kafka;

import org.junit.Assert;
import org.junit.Test;


public class KafkaEventGetTest {

    @Test
    public void callOkWithoutException() throws Exception {

        KafkaEvent kafkaEvent = new KafkaEvent();
        kafkaEvent.setResultException(false);

        KafkaObject kafkaObject = new KafkaObject();
        kafkaEvent.setResult(kafkaObject);
        kafkaObject.setFullQualifiedClassName(String.class.getName());
        kafkaObject.set("AA");


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
