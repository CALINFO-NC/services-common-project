package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaPublishMethodResult;
import com.calinfo.api.common.kafka.KakfaAnnotationTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@Profile("kafka")
public class KakfaTestController {


    @PostMapping
    public KakfaAnnotationTest.TestResource create(@RequestBody KakfaAnnotationTest.TestResource testResource) {
        return testResource;
    }

    @PutMapping
    public KakfaAnnotationTest.TestResource modify(@RequestBody KakfaAnnotationTest.TestResource testResource) {
        throw new MessageStatusException(HttpStatus.FORBIDDEN, "erreur");
    }
}
