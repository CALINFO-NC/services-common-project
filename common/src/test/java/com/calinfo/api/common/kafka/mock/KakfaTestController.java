package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.ex.MessageStatusException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/TestResource")
@Profile("kafka")
public class KakfaTestController {

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TestResource read(@PathVariable("id") String id) {
        throw new MessageStatusException(HttpStatus.FORBIDDEN, "erreur");
    }

}
