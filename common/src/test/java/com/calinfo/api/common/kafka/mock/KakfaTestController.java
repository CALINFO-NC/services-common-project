package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.kafka.KafkaPublishMethodResult;
import com.calinfo.api.common.kafka.KakfaAnnotationTest;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@Profile("kafka")
public class KakfaTestController {

    @KafkaPublishMethodResult
    @PostMapping
    public KakfaAnnotationTest.TestResource create(KakfaAnnotationTest.TestResource resource) {
        return resource;
    }

    @GetMapping
    public KakfaAnnotationTest.TestResource list() {
        return new KakfaAnnotationTest.TestResource("ouh");
    }
}
