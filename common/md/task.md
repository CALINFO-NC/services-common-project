
# Description

Ce chapitre traite des façons d'exécuter du code en changeant les éléments de contexte suivants…
* L'utilisateur connecté
* Les rôles de l'utilisateur connecté
* Le domaine

Explication avec un cas d'usage

```java  
	taskRunner.run("login", "domain", new String[]{"role1", "role2"}, () -> {  
		...
	});  
```  

Si le développeur ne précise pas l'utilisateur connecté, la tâche sera exécutée avec par défaut l'utilisateur *securityProperties.getSystemLogin()* (Voir configuration sur la sécurité pour plus de détails).
