# Description

Le common embarque une sécurité cablé avec Keycloak (https://www.keycloak.org/).

# Mise en oeuvre

La sécurité prend en charge la gestion du multi-tenant et utilise keyckloak pour son fonctionnement. 
Le common injectera le domain directement dans le realm de la configuration keycloak

Exemple de configuration
```
keycloak:
  realm: ""   # ici le common remplacera cette valeur par la valeur du domain
  auth-server-url: http://localhost:8085/auth   # Serveur d'authentification
  resource: login-app   # Client keycloak (nom de l'application dans keycloak)
  public-client: true
  use-resource-role-mappings: true
  principal-attribute: preferred_username
```

Il faut aussi écrire la classe Java qui implémente *com.calinfo.api.common.security.CommonKeycloakSecurityConfigurerAdapter*.

Exemple (Prennez note de l'annotation @KeycloakConfiguration):
```
import com.calinfo.api.common.security.CommonKeycloakSecurityConfigurerAdapter;
import com.calinfo.api.common.security.HostResolver;
import com.calinfo.api.common.security.SecurityProperties;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@KeycloakConfiguration
public class SecurityConfig extends CommonKeycloakSecurityConfigurerAdapter {

    public SecurityConfig(SecurityProperties securityProperties,
                          KeycloakSpringBootProperties adapterConfig,
                          HostResolver hostResolver){
        super(securityProperties, adapterConfig, hostResolver);
    }
    
    // Exemple de configuration de la sécurité
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.authorizeRequests().regexMatchers("/private/*").hasRole(securityProperties.getAccessAppRole()).anyRequest().permitAll();
        http.csrf().disable();
    }
}
```

# Déseactiver la sécurité

Pour désactiver totalement la sécurité de votre application, vous devrez définir la configuration ci-dessous
```
keycloak:
  enabled: false
```

Il faudra aussi désactivé la sécurity spring en utilisant l'annotation *@SpringBootApplication(exclude = SecurityAutoConfiguration.class)*

Exemple :
```

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Application extends SpringBootServletInitializer {

    ...

}
```
