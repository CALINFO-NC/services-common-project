package com.calinfo.api.common.security;

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
    private String publicKeyValue = null;

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
