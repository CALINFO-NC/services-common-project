# Description

Le common embarque actuator.

# Mise en oeuvre

Pour le mettre en œuvre, il vous suffit de spécifier le nom d'utilisateur et le mot de passe pour accéder aux API actuator. 
Dans le cas contraire, ils seront générés de manière aléatoire à chaque appel de l'API. 
Pour définir ces identifiants et mots de passe, veuillez fournir la configuration suivante :

Exemple de configuration
```
common:
  configuration:
    security:
      actuator:
        login: monLogin
        password: monMotDePasse
```

Ensuite, il est nécessaire de définir une configuration Spring. Voici la configuration recommandée :

```
management:
  endpoints:
    web:
      exposure:
        include: "*"
    health:
      show-details: always
  endpoint:
    env:
      keys-to-sanitize:
        - password
        - secret
        - key
        - token
        - .*credentials.*
        - vcap_services
        - sun.java.command
        - private_key_certificat
    configprops:
      keys-to-sanitize:
        - password
        - secret
        - key
        - token
        - .*credentials.*
        - vcap_services
        - sun.java.command

```
