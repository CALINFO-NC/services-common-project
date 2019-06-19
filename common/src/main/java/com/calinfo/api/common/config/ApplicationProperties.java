package com.calinfo.api.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by dalexis on 06/01/2018.
 */
@ConfigurationProperties(prefix = "common.configuration.application")
@Component // Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
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
