# Description

 Le *common* apporte une API permettant au développeur d'écrire ses classes représentant des tâches d'éxécution en précisant :

 * L'utilisateur exécutant cette tâche.

 * Le *domain* associé dans lequel se lance la tâche

 * Les rôles de l'utilisateur exécutant la tâche


```
Exemple :

taskRunner.run("login", "domain", new String[]{"role1", "role2"}, () -> {
    ...
});
```

 Si le développeur ne précise pas l'utilisateur connecté, la tâche sera exécutée avec par défaut l'utilisateur *securityProperties.getSystemLogin()* (Voir configuration sur la sécurité pour plus de détails).
