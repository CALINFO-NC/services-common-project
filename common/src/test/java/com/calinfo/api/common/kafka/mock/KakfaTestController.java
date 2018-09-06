package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaIgnore;
import com.calinfo.api.common.kafka.KakfaAnnotationTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping
    @KafkaIgnore
    public List<KakfaAnnotationTest.TestResource> list(ServletRequest request) {
        return new ArrayList<>();
    }


    @GetMapping(value = {"publishingGet"})
    public List<KakfaAnnotationTest.TestResource> listPublishing(@KafkaIgnore ServletRequest request) {
        return new ArrayList<>();
    }


    @PutMapping(value = {"voidReturn"})
    public void voidReturn(@RequestBody KakfaAnnotationTest.TestResource testResource, @KafkaIgnore ServletRequest request) {
        // Test au cas ou un controlleur renvoi void
    }
}
