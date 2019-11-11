package com.calinfo.api.common;

import com.calinfo.api.common.kafka.mock.KafkaServiceImpl;
import com.calinfo.api.common.kafka.mock.KafkaSubServiceImpl;
import com.calinfo.api.common.kafka.mock.Receiver;
import com.calinfo.api.common.mocks.MockApiKeyManager;
import com.calinfo.api.common.mocks.MockMessageServiceImpl;
import com.calinfo.api.common.mocks.MockServiceConstrainteVilationService;
import com.calinfo.api.common.tenant.TenantProperties;
import com.calinfo.api.common.tenant.TenantTestFilter;
import com.calinfo.api.common.tenant.service.TableDomainServiceImpl;
import com.calinfo.api.common.tenant.service.TableGenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@TestConfiguration
public class AutowiredConfig {

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Bean
    @Profile("apiKeyManager")
    public MockApiKeyManager mockApiKeyManager() {
        MockApiKeyManager obj = new MockApiKeyManager();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    public MockMessageServiceImpl mockMessageServiceImpl() {
        MockMessageServiceImpl obj = new MockMessageServiceImpl();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    @Validated
    public MockServiceConstrainteVilationService mockServiceConstrainteVilationService() {
        MockServiceConstrainteVilationService obj = new MockServiceConstrainteVilationService();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    @Profile("kafka")
    public Receiver receiver() {
        Receiver obj = new Receiver();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    @ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
    public TenantTestFilter tenantTestFilter() {
        TenantTestFilter obj = new TenantTestFilter();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }

    @Bean
    public KafkaServiceImpl kafkaServiceImpl() {
        KafkaServiceImpl obj = new KafkaServiceImpl();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }


    @Bean
    public KafkaSubServiceImpl kafkaSubServiceImpl() {
        KafkaSubServiceImpl obj = new KafkaSubServiceImpl();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }


    @Bean
    public TableDomainServiceImpl tableDomainServiceImpl() {
        TableDomainServiceImpl obj = new TableDomainServiceImpl();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }


    @Bean
    public TableGenericServiceImpl tableGenericServiceImpl() {
        TableGenericServiceImpl obj = new TableGenericServiceImpl();
        autowireCapableBeanFactory.autowireBean(obj);
        return obj;
    }


}
