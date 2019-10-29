package com.calinfo.api.common.io.storage.connector.google;

/*-
 * #%L
 * common-io
 * %%
 * Copyright (C) 2019 CALINFO
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "common-io.storage.connector.configuration")
@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "google")
// Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
public class GoogleConfigProperties {

    private String projectId;

    private String credentials;

    private String buckatName;

    /**
     * Préfixe des noms d'espace dans google
     */
    private String prefixSpaceName = "domain_";

    /**
     * Nom du buckate utilisé lorsque qu'il n'est pas précisé dans le connecteur
     */
    private String defaultSpaceName = "public";
}
