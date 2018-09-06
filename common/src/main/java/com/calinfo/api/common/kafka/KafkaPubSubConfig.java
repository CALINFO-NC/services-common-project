package com.calinfo.api.common.kafka;

import java.util.Map;

/**
 * Configuration par défault des producteurs et consommateurs kafka
 */
public interface KafkaPubSubConfig {

    Map<String, Object> producerConfigs();

    Map<String, Object> consumerConfigs();
}
