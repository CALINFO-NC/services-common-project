package com.calinfo.api.common.io;

import com.calinfo.api.common.io.storage.mock.MockBinaryDataConnector;
import com.calinfo.api.common.io.storage.mock.MockBinaryDataDomainService;
import com.calinfo.api.common.io.storage.mock.MockBinaryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class AutowiredConfig {

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Bean
    @Profile("storage")
    public MockBinaryDataConnector mockBinaryDataConnector() {
        MockBinaryDataConnector obj = new MockBinaryDataConnector();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    public MockBinaryDataDomainService mockBinaryDataDomainService() {
        MockBinaryDataDomainService obj = new MockBinaryDataDomainService();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    public MockBinaryDataService mockBinaryDataService() {
        MockBinaryDataService obj = new MockBinaryDataService();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

}
