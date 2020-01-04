package com.calinfo.api.common.security;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
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
 * Propriété de configuration de la sécurité
 */
@ConfigurationProperties(prefix = "common.configuration.security")
@Component // Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
@Getter
@Setter
public class SecurityProperties {

    public static final String DEFAULT_ANONYMOUS_USER_LOGIN = "Anonymous";

    public static final String DEFAULT_SYSTEM_USER_LOGIN = "System";

    /**
     * Activer la gestion de la sécurité
     */
    private boolean enabled = false;

    /**
     * Valeur de la clé public permettant de décrypter le jeton JWT
     */
    private String publicKeyCertificat = null;

    /**
     * Regex des url privée
     */
    private String privateUrlRegex = "^(\\/api\\/v.*\\/private\\/.*)";

    /**
     * Lorsqu'un utilisateur n'est pas connecté, le pricipal peut renvoyer un login par défaut
     */
    private String anonymousLogin = DEFAULT_ANONYMOUS_USER_LOGIN;

    /**
     * Lorsqu'une tâche système est lancée, le pricipal peut utiliser un login par défaut
     */
    private String systemLogin = DEFAULT_SYSTEM_USER_LOGIN;
}
