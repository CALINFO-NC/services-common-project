package com.calinfo.api.common.manager;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dalexis on 05/01/2018.
 */
@Lazy
@Component
public class RestTemplateManager {

    private ReentrantLock locker = new ReentrantLock();
    private RestTemplate restTemplate = null;

    public RestTemplate getRestTemplate(){

        if (restTemplate == null){

            locker.lock();

            try {

                if (restTemplate == null) {
                    restTemplate = new RestTemplate();
                }
            }
            finally {
                locker.unlock();
            }
        }

        return restTemplate;
    }
}
