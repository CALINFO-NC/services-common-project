package com.calinfo.api.common.tenant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by dalexis on 06/01/2018.
 */
@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@ConfigurationProperties(prefix = "common.configuration.tenant")
@Component  // Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
@Getter
@Setter
public class TenantProperties {


    public static final String CONDITIONNAL_PROPERTY = "common.configuration.tenant.enabled";

    /**
     * Activer la gestion de multi schméa (un schma par demaine)
     */
    private boolean enabled = false;

    /**
     * Nom du schéma par défaut (ou générique) de l'application
     */
    private String defaultValue = "public";

    /**
     * Préfixe des noms de schémas représentant chaque domaine. Le sufixe est le nom du domaine lui même
     */
    private String prefix = "domain_";

    /**
     * Nom du gestionnaire de presistence pour les transaction dans la datasource standard (spring.datasource)
     */
    private String persistenceName = "default";

    /**
     * Configuration liquibase
     */
    private LiquibaseProperties liquibase = null;

    /**
     * Configuration JPA
     */
    private JpaProperties jpa = null;

    /**
     * Information de scan sur le schéma du domaine
     */
    private String[] domainScanEntities;

    /**
     * Information de scan sur le schéma par défaut
     */
    private String[] genericScanEntities;

}
