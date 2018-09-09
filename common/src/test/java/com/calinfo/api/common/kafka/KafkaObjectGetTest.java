package com.calinfo.api.common.kafka;

import com.calinfo.api.common.kafka.mock.KafkaService;
import com.calinfo.api.common.kafka.mock.Receiver;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.rule.KafkaEmbedded;


public class KafkaObjectGetTest {


    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true);

    @Autowired
    private KafkaService kafkaService;

    @LocalServerPort
    private int port;

    @Test
    public void callOk() {

        String src = "AA";

        KafkaObject kafkaObject = new KafkaObject();
        kafkaObject.setFullQualifiedClassName(String.class.getName());
        kafkaObject.setValue(src);

        String cible = kafkaObject.get();

        Assert.assertEquals(cible, src);
    }

    @Test
    public void callKo() {

        KafkaObject kafkaObject = new KafkaObject();
        kafkaObject.setFullQualifiedClassName(String.class.getName());
        kafkaObject.setValue("AA");

        try {
            int cible = kafkaObject.get();
            Assert.fail("La méthode ci-dessus, ne devrait pas marcher");
        }
        catch(Exception e){
            // On ne fait rien
        }
    }

}
