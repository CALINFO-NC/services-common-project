package com.calinfo.api.common.kafka;

import com.calinfo.api.common.kafka.mock.Receiver;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("kafka")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringKafkaApplicationTest {


    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true);

    @LocalServerPort
    private int port;

    @Test
    public void callGetOk() throws Exception {

        String uri = "/api/v1/TestResource/2";
        callGet(uri);
    }

    @Test
    public void callGetKo() throws Exception {

        String uri = "/api/v1/TestResource/1";
        callGet(uri);
    }


    private void callGet(String uri) throws Exception {

        receiver.clearKafkaEvent();
        String url = String.format("http://localhost:%s%s", port, uri);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();

        KafkaEvent kafkaEvent = receiver.getKafkaEvent();
        Assert.assertNotNull(kafkaEvent);
        Assert.assertEquals(kafkaEvent.getTopic(), Receiver.TOPIC);
        Assert.assertTrue(kafkaEvent.getResponse().getStatus().intValue() == response.getStatusLine().getStatusCode());

        for (Header header : response.getAllHeaders()){

            String headerValue = kafkaEvent.getResponse().getHeaders().get(header.getName());
            Assert.assertNotNull(headerValue);
            Assert.assertEquals(headerValue, header.getValue());
        }

        Assert.assertEquals(kafkaEvent.getResponse().getBody(), EntityUtils.toString(entity));
    }

}
