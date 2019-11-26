package com.calinfo.api.common.io.storage.connector.ftp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "common-io.storage.connector.configuration")
@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "ftp")
// Ici on utilise pas @Configuration (voir https://stackoverflow.com/questions/53484529/inspection-info-verifies-configurationproperties-setup-new-in-2018-3-intellij)
public class FtpConfigProperties {

    /**
     * Host ou adresse IP du serveur FTP
     */
    private String host;

    /**
     * Port FTP
     */
    private int port = 21;

    /**
     * Login
     */
    private String username;

    /**
     * Mot de passe
     */
    private String password;

    /**
     * Répertoire racine du lieu d'enregistrement des fichiers
     */
    private String path;

    /**
     * Préfixe des noms d'espace dans google
     */
    private String prefixSpaceName = "domain_";

    /**
     * Nom du buckate utilisé lorsque qu'il n'est pas précisé dans le connecteur
     */
    private String defaultSpaceName = "public";

    /**
     * Activer ou non le mode passif
     */
    private boolean passivMode = true;

}
