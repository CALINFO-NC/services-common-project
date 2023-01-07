package com.calinfo.api.common.config;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
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
