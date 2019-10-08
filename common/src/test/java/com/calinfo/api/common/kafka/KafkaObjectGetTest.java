package com.calinfo.api.common.kafka;

import org.testng.Assert;
import org.testng.annotations.Test;


public class KafkaObjectGetTest {

    @Test
    public void callOk() throws Exception {

        String src = "AA";

        KafkaObject kafkaObject = new KafkaObject();
        kafkaObject.setFullQualifiedClassName(String.class.getName());
        kafkaObject.set(src);

        String cible = kafkaObject.get();

        Assert.assertEquals(cible, src);
    }

    @Test
    public void callKo() throws Exception {

        KafkaObject kafkaObject = new KafkaObject();
        kafkaObject.setFullQualifiedClassName(String.class.getName());
        kafkaObject.set("AA");

        try {
            int cible = kafkaObject.get();
            Assert.fail("La méthode ci-dessus, ne devrait pas marcher");
        }
        catch(Exception e){
            // On ne fait rien
        }
    }

}
