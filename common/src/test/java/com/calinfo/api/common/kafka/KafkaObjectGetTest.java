package com.calinfo.api.common.kafka;

import org.junit.Assert;
import org.junit.Test;


public class KafkaObjectGetTest {

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
