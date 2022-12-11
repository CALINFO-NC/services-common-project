# Introduction

 Le *common* est un module créé spécialement pour les applications de type *service*.
 Il apporte un certain nombre de fonctionnalités et de librairies décritent dans chacun des modules.

 Pour utiliser le common dans une application de type *service*, il suffit d'hériter de :

```
<parent>
    <groupId>com.calinfo-nc.services</groupId>
    <artifactId>common-project</artifactId>
    <version>...</version>
</parent>
```

# Listes des modules

 * [common](./common)
 * [common-api](./common-api)
 * [common-test](./common-test)
 * [common-io](./common-io)
 * [common-teavm](./common-teavm)

# Licence

Projet sous licence GNU GPL V3 : [http://www.gnu.org/licenses/gpl-3.0.html](http://www.gnu.org/licenses/gpl-3.0.html)

Binaire disponnible dans le "repository" :  [https://calinfo.jfrog.io/calinfo/license-gpl-local](https://calinfo.jfrog.io/calinfo/license-gpl-local)

# Quoi de nouveau

 * Passage à la version 2.7.6 de spring boot
 * **net.javacrumbs.shedlock.core.SchedulerLock** à été remplacé par **net.javacrumbs.shedlock.spring.annotation.SchedulerLock**
 * Mise à jour de toutes les versions des dépendances
 * ATTENTION : 
   * h2 : Pour des raisons de rétro-compatibilité, on reste sur la version 1.4.200
   * Keycloak : Est resté en version 16.1.1

