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

 * Mise à jour des versions des dépendances dans les pom.xml
 * La classe com.calinfo.api.common.resource.Resource a été déprécié
 * Le common ne prend plus en charge la configurattion swagger. Cependant, vous pouvez garder sa 
 rétrocompatibilité en activant la propriété **common.deprecated.swagger.enabled** à **true**.
 Il sera aussi nécessaire de tirer les dépendances ci-dessous :
 ```
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
``` 
