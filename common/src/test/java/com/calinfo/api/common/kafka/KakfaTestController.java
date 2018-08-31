package com.calinfo.api.common.kafka;

import org.springframework.web.bind.annotation.RestController;

@KakfaTopicInitializer(methods = {"create"})
@RestController
class KakfaTestController {

    @KafkaPublishMethodResult
    public KakfaAnnotationTest.TestResource create(KakfaAnnotationTest.TestResource resource) {
        return new KakfaAnnotationTest.TestResource();
    }
}
