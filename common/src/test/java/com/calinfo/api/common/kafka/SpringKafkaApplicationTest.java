package com.calinfo.api.common.kafka;

import com.calinfo.api.common.kafka.mock.Receiver;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("kafka")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringKafkaApplicationTest {


    @Autowired
    private Receiver receiver;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, "COMMON.default.v1.kakfa-test-controller.read");

    @LocalServerPort
    private int port;


    @Ignore
    @Test
    public void testReceive() throws Exception {
        //kafkaTemplate.send(BOOT_TOPIC, "Hello Boot!");

        String uri = "/api/v1/TestResource/1";

        String url = String.format("http://localhost:%s%s", port, uri);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(url));

        //mockMvc.perform(httpRequest);
        //SwaggerItemCollector swaggerItemCollector = swaggerCollector.getAll().stream().filter(s -> s.getUri().equals(uri)).collect(Collectors.toList()).get(0);
        //String topicName = kafkaTopicNameResolver.getTopicName(swaggerItemCollector);


        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);
    }
}
