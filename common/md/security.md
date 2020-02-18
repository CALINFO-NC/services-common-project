# Description

 Lors de l'appel d'une requête HTTP d'un service, le *common* tentera de déterminer l'identité de l'utilisateur ayant appelé cette requête.
 Si le *common* ne parvient pas à identifier l'utilisateur, ce dernier sera par défaut *securityProperties.getAnonymousLogin()* dans le cas d'une requête publique,
 ou cela génèrera une exception dans le cas d'une requête privée (Voir configuration sur la sécurité pour plus de détails).

 Le *common* détermine l'utilisateur appelant une requête en décryptant la valeur du *token* JWT préfixé par "*Bearer *" (avec l'espace) du *Header "Authorization"*.
 S'il ne parvient pas à décrypter le token parce qu'il est périmé, alors il délègue le rafraichissement du token à *com.calinfo.api.common.manager.ApiKeyManager.refreshToken(apiKey)*
 qui est chargé de renvoyer un nouveau token à jour. le paramètre *apiKey* est récupéré du *Header "X-ApiKey"* de la requête HTTP.
 A chaque requête, le *common* renvoie dans le *Header "Authorization"* de la réponse le token actualisé si necessaire.

 ATTENTION : il est important lorsque l'on utilise la sécurité du *common* que l'injection de *com.calinfo.api.common.manager.ApiKeyManager* soit possible.

```
Exemple de classe pouvant être injectée

import com.calinfo.api.common.manager.ApiKeyManager;
import org.springframework.stereotype.Component;

@Component
public class MyApiKeyManager implements ApiKeyManager{

    @Override
    public String refreshToken(String apiKey) {
        return ...
    }
}
```

 La forme décryptée du *token* est représentée par la classe *com.calinfo.api.common.security.JwtUser*.

 Le *common* apporte aussi des outils via la classe utilitaire *com.calinfo.api.common.utils.SecurityUtils* permettant de crypter ou décrypter un *token*.

 Dans le reste de l'application, il est possible de récupérer l'utilisateur connecté :

 * Soit par injection :

```
@Autowired
private PrincipalManager principalManager;

...

AbstractCommonPrincipal principal = principalManager.getPrincipal();
```

 * Soit par programme

```
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
AbstractCommonPrincipal principal = (AbstractCommonPrincipal)auth.getPrincipal();
```

 Il existe aussi "PrincipalFactory" qui permet aussi d'insérer dans le context un "principal" à partir de ses tokens, ou d'insérer un "principal" annonyme.


# Configuration

 La configuration de la sécurité se fait dans la sous configuration *common.configuration.security* du fichier *yaml* .
 Toutes les propriétés de cette sous configuration sont décrites dans la JavaDoc de la classe *com.calinfo.api.common.security.SecurityProperties*
 De plus, le common base sa sécurité sur un filtre, donc, si vous activé la sécurité, il faudra ajouter l'annotation SpringBootApplication comme précisé dans l'exemple ci-dessous

```
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MocksApplication {
    ...
}
```