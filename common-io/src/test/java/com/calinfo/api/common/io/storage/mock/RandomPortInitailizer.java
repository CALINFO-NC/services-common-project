package com.calinfo.api.common.io.storage.mock;


import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.util.TestSocketUtils;

public class RandomPortInitailizer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        int randomPort = TestSocketUtils.findAvailableTcpPort();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "common-io.storage.connector.configuration.port=" + randomPort);
    }

}
