# Description

Le common embarque une sécurité cablé avec Keycloak (https://www.keycloak.org/).

# Mise en oeuvre

La sécurité prend en charge la gestion du multi-tenant et utilise keyckloak pour son fonctionnement. 
Le common injectera le domain directement dans le realm de la configuration keycloak

Exemple de configuration
```
keycloak:
  # realm: ""   # cette valeur n'a pas besoin d'être spécifiée. Le common alimentera cette clef avec la valeur du domain
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

Pour désactiver la sécurité avec Keycloak, vous devrez définir la configuration ci-dessous
```
keycloak:
  enabled: false
```

Vous pouvez aussi désactivé le méchanisme de sécurité spring en utilisant l'annotation *@SpringBootApplication(exclude = SecurityAutoConfiguration.class)*

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

# Comment intégrer une une sécurité "httpBasic" délégué à spring tout en utilisant keycloak ?

Un cas d'utilisation est par exemple d'utiliser la gestion de la sécurité à keycloak pour toutes
les urls "/api/v*/private/**" et de déléguer les urls "/actuator/**" à l'authentification basic.

##1. Créer une implémentation de *AuthenticationProvider*

```
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;

@RequiredArgsConstructor
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private final String user;
    private final String password;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials()
                .toString();

        if (user.equals(username) && password.equals(password)) {
            return new UsernamePasswordAuthenticationToken
                    (username, password, Collections.emptyList());
        } else {
            throw new BadCredentialsException("External system authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
```

##2. Surcharger la classe *CommonKeycloakSecurityConfigurerAdapter*

```
import com.calinfo.api.common.security.CommonKeycloakSecurityConfigurerAdapter;
import com.calinfo.api.common.tenant.DomainResolver;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.QueryParamPresenceRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@KeycloakConfiguration
public class SecurityConfig extends CommonKeycloakSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    public SecurityConfig(KeycloakSpringBootProperties adapterConfig,
                          DomainResolver domainResolver,
                          KeycloakSpringBootProperties keycloakSpringBootProperties,
                          SecurityProperties securityProperties){
        super(adapterConfig, domainResolver, keycloakSpringBootProperties.getResource());
        this.securityProperties = securityProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v*/private/**").hasRole("CLOUDPRINT")
                .antMatchers("/actuator/**").authenticated()
                .and()
                .httpBasic();
    }

    @Bean
    @Override
    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
        RequestMatcher requestMatcher =
                new OrRequestMatcher(
                        new AntPathRequestMatcher("/sso/login"),
                        new QueryParamPresenceRequestMatcher(OAuth2Constants.ACCESS_TOKEN),
                        new RequestMatcher(){
                            public boolean matches(HttpServletRequest request) {
                                String authorizationHeaderValue = request.getHeader("Authorization");
                                return authorizationHeaderValue != null && !authorizationHeaderValue.startsWith("Basic ");
                            }
                        }
                );
        return new KeycloakAuthenticationProcessingFilter(authenticationManagerBean(), requestMatcher);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new BasicAuthenticationProvider(securityProperties.getUser().getName(), securityProperties.getUser().getPassword()));
    }
}
```