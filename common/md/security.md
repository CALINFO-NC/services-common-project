# Description

Le common embarque une sécurité cablé avec Keycloak (https://www.keycloak.org/).
Toutes les propriétés paramétrables par le common sur la sécurité sont décrite dans la classe *com.calinfo.api.common.security.SecurityProperties*
Par défaut la sécurité du common est activé. Pour la désactiver, il faut mettre à *false* la configuration *common.configuration.security.enabled*.
Cependant, cette configuration désactivera uniquement la pris en charge de la sécurité par le common, et vous devrez configurer à lamain la sécurité de keycloak.
Pour désactiver totalement la sécurité de votre application, vous devrez définir la configuration ci-dessous
```
common:
  configuration:
    security:
      enabled: false
keycloak:
  enabled: false
```


La sécurité du common délègue la configuration de keyloack au fichier *commonkeycloak.json* au lieu du fichier usuel *keycloak.json* définit dans la doc Keycloak.
Le contenue du fichier *commonkeycloak.json* est le même que celui du fichier *keycloak.json* à la diférrence que la valeur *realm* sera surcharger par le common avec la valeur du domain
La sécurité du common prend aussi en charge la définition des URLs sécurisées définit dans *common.configuration.security.privateUrlRegex*

Exemple de fichier *commonkeycloak.json*
```
  {
    "realm": "",        -> ici le common remplacera cette valeur par la valeur du domain
    "auth-server-url" : "http://localhost:8085/auth",     -> Serveur d'authentification
    "resource" : "login-app",       -> Client keycloak (nom de l'application dans keycloak)
    "public-client" : true,
    "principal-attribute" : "preferred_username"
  }
```

Pour définir le domain dans lequel les reqêtes sécurisées devront intervenir, il faut définir le header *X-Domain* avec la valeur du domain d'intervention
