package com.calinfo.api.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dalexis on 06/01/2018.
 */
@ConfigurationProperties(prefix = "common.configuration.application")
@Configuration
@Getter
@Setter
public class ApplicationProperties {

    /**
     * Identifiant de l'application
     */
    private String id;

    /**
     * Nom de l'application
     */
    private String name;

    /**
     * Version de l'application
     */
    private String version;

    /**
     * Description de l'application
     */
    private String description;
}
