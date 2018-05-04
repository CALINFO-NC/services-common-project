package com.calinfo.api.common.tenant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 06/01/2018.
 */
@ConditionalOnProperty("common.configuration.tenant.enable")
@ConfigurationProperties(prefix = "common.configuration.tenant")
@Configuration
@Getter
@Setter
public class TenantProperties {

    /**
     * Activer la gestion de multi schméa (un schma par demaine)
     */
    private boolean enable = false;

    /**
     * Nom du schéma par défaut de l'application
     */
    private String defaultValue = "public";

    /**
     * Préfixe des noms de schémas, le sufixe est le domaine
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
    private ScanProperties tenantScan = new ScanProperties();

    /**
     * Information de scan sur le schéma par défaut
     */
    private ScanProperties defaultScan = new ScanProperties();
}
