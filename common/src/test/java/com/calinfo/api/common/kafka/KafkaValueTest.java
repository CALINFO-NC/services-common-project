package com.calinfo.api.common.kafka;

import org.testng.Assert;
import org.testng.annotations.Test;


public class KafkaValueTest {

    @Test
    public void callOkWithoutException() throws Exception {


        KafkaEvent kafkaEvent = new KafkaEvent();
        KafkaData data = new KafkaData();
        kafkaEvent.setData(data);
        KafkaMetadataService metadataService = new KafkaMetadataService();
        kafkaEvent.setMetadataService(metadataService);

        data.setReturnValueException(false);
        metadataService.setReturnType(String.class.getName());
        metadataService.getParametersTypes().put(0, Integer.class.getName());
        data.setSerializedReturnValue(KafkaUtils.serialize("AA"));
        data.getSerializedParametersValues().put(0, KafkaUtils.serialize(3));

        try {
            String cibleA = kafkaEvent.getValues().getReturnValue(String.class);
            Assert.assertEquals(cibleA, "AA");

            cibleA = kafkaEvent.getValues().getReturnValue();
            Assert.assertEquals(cibleA, "AA");

            Assert.assertEquals(kafkaEvent.getValues().getParametersSize(), 1);

            Integer cible1 = kafkaEvent.getValues().getParameterValueAt(0, Integer.class);
            Assert.assertEquals(cible1, 3);

            cible1 = kafkaEvent.getValues().getParameterValueAt(0);
            Assert.assertEquals(cible1, 3);


        } catch (Exception e) {
            Assert.fail("Il ne devrait pas y avoir d'exception de levée");
        }
    }

    @Test
    public void callOkException() {



        KafkaEvent kafkaEvent = new KafkaEvent();
        KafkaData data = new KafkaData();
        kafkaEvent.setData(data);
        KafkaMetadataService metadataService = new KafkaMetadataService();
        kafkaEvent.setMetadataService(metadataService);

        data.setReturnValueException(true);
        metadataService.setReturnType(NullPointerException.class.getName());


        try {
            kafkaEvent.getValues().getReturnValue(String.class);

            Assert.fail("Il devrait avoir une exception de levée");

        } catch (KafkaException e) {

            if (!(e.getCause() instanceof NullPointerException)){
                Assert.fail("Le type d'exception devrait être un NullPointerException");
            }

        }
    }


}
