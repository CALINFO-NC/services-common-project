package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.kafka.KafkaTopicPrefix;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@KafkaTopicPrefix("Pre")
public class KafkaService2Impl extends AbstractKafkaService2Impl<TestResource2<TestResource>> {

}
