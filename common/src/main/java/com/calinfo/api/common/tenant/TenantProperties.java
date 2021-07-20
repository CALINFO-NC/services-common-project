package com.calinfo.api.common.tenant;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import lombok.Getter;
import lombok.Setter;
import org.hibernate.MultiTenancyStrategy;
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
@Component
// Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
@Getter
@Setter
public class TenantProperties {


    public static final String CONDITIONNAL_PROPERTY = "common.configuration.tenant.enabled";
    public static final String MULTITENANCY_STRATEGY = "common.configuration.tenant.multitenancyStrategy";

    private String multitenancyStrategy = MultiTenancyStrategy.SCHEMA.name();

    /**
     * Activer la gestion de multi schméa (un schma par demaine)
     */
    private boolean enabled = false;

    /**
     * Nom du schéma/bdd par défaut (ou générique) de l'application
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

    /**
     * Stratégie multi tenant
     */
    private MultiTenantStrategy multiTenantStrategy = MultiTenantStrategy.SCHEMA;

}
