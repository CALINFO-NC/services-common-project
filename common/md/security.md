# Description

Le common embarque une sécurité cablé avec Keycloak (https://www.keycloak.org/).

# Mise en oeuvre

La sécurité prend en charge la gestion du multi-tenant et utilise keyckloak pour son fonctionnement. 
Le common injectera le realm de la configuration keycloak

Exemple de configuration
```
common:
  configuration:
    security:
      keycloak:
        baseUrl: http://host.docker.internal:8106
        client-id: '@project.artifactId@'
        urls:       # (facultatif) Un certain nombre d'URL ici est paramétrable. Voir la classe KeycloakUrlProperties
          login: /login
          logout: /protected/api/v1/logout
```

Il faut aussi écrire la classe Java qui implémente *com.calinfo.api.common.security.keycloak.KeycloakAuthorizeHttpRequestsCustomizerConfig*....

Exemple :
```
import com.calinfo.api.common.security.keycloak.KeycloakAuthorizeHttpRequestsCustomizerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class KeycloakSecurityConfig implements KeycloakAuthorizeHttpRequestsCustomizerConfig {


    private RequestMatcher getKeycloakRequestMatcher(){
        return new OrRequestMatcher(
            new AntPathRequestMatcher("/protected/**"),
            new AntPathRequestMatcher("/data/**"),
            new AntPathRequestMatcher("/jsapplication/sencha/extjs/genesis/**")
        );
    }

    @Override
    public void config(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry request) {
        request
                .requestMatchers(getKeycloakRequestMatcher())
                .authenticated()
                .anyRequest()
                .permitAll();
    }
}
```

Pour finir il faut Implémenter l'interfact KeycloakManager.
Voici un exemple d'implémentation :
```
import com.calinfo.api.common.security.keycloak.KeycloakManager;
import com.calinfo.api.common.security.keycloak.KeycloakProperties;
import com.calinfo.api.common.tenant.TenantUrlFilter;
import com.calinfo.api.keycloak.GenesisKeycloakProperties;
import lombok.Getter;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(KeycloakManagerImpl.ORDER_FILTER)
public class KeycloakManagerImpl implements KeycloakManager {

    public static final int ORDER_FILTER = TenantUrlFilter.ORDER_FILTER + 100;

    @Getter
    private final Keycloak rootHandle;


    public KeycloakManagerImpl(GenesisKeycloakProperties genesisKeycloakProperties,
                               KeycloakProperties keycloakProperties) {


        this.rootHandle = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getBaseUrl())
                .realm("master")
                .username(genesisKeycloakProperties.getRoot().getLogin())
                .password(genesisKeycloakProperties.getRoot().getPassword())
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(100).build())
                .build();
    }
}
```

Vous remarquerez, que ce composant doit être démaré après le composant TenantUrlFilter.

# URLs par défaut 

Le module common intègre par défaut des URLs (voir la classe KeycloakUrlProperties pour les valeurs par défaut). 
Ces adresses sont les suivantes :

* Connexion : Lien permettant de se connecter.
* Déconnexion : Lien permettant de se déconnecter.
* Informations JSON de l'utilisateur connecté : Renvoie l'utilisateur connecté avec ses rôles au format JSON.
* Profil de l'utilisateur : Lien Keycloak vers la page du profil de l'utilisateur. Vous pouvez ajouter à cette URL les pages du profil Keycloak (par exemple : url + "/personal-info").
* Console d'administration : Lien Keycloak pour accéder à la console d'administration de Keycloak. Vous pouvez ajouter à cette URL les pages de la console de Keycloak (par exemple : url + "/users" sans le realm).
