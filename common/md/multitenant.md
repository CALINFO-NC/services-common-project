# Description

 La gestion *multi-tenant* implémentée par le *common* est un mécanisme permettant, pour une seule instance d'une application :
 * D'avoir plusieurs schémas d'une même structure de base de données. 
 * De connaitre le royaume (realm) à utilisé pour la connexion à keycloak

Dans le reste du *common*, nous avons appelé *domain* une représentation de ce schéma, et *realm* une représentation du royaume. 

# Domaine

Le *common* sait gérer une base de données commune pour tous les domaines. Cette base de données (ou schéma) est appelé schéma générque.

Exemple de cas d'utilisation : Avoir une table commune de devises pour chaque *domain*. Inutile de dupliquer cette table dans chacun des *domain*. Il est préférable que celle-ci soit partagée.

## Comment indiquer quel est le domaine à utiliser en fonction de l'URL

Le module *common* requiert l'implémentation de la classe DomainResolver afin de déterminer quel domaine utiliser en fonction de l'URL.

Cependant, le module *common* propose une implémentation par défaut qui indique que le nom de domaine correspond au nom présent dans l'URL. Par exemple, pour l'URL http://www.calinfo-nc.com/restDeMonUrl, le nom de domaine sera www.calinfo-nc.com.

Vous avez la possibilité de personnaliser ce comportement en créant votre propre implémentation de DomainResolver.

Exemple :
```
import com.calinfo.api.common.domain.DomainResolver;
import com.calinfo.api.common.tenant.Request;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

@Pimary
@Component
public class MyDomainResolverImpl implements DomainResolver {

    public String getDomain(Request request){
        return ...;
    }
}
```

## Comment le *common* sait-il quels sont les *repository* et les *entity* utilisés pour un *domain* et ceux utilisés pour la base de données commune (ou générique) ?

 Le développeur doit indiquer ces informations au *common*.

  * Pour indiquer les *entity* pour le *domain* le développeur doit, dans le fichier *yaml*, définir la configuration *common.configuration.domain.domainScanEntities*

  * Pour indiquer les *entity* pour la base de données générique, le développeur doit, dans le fichier *yaml*, définir la configuration *common.configuration.domain.genericScanEntities*.

  * Pour indiquer les *jpaRepository* pour le *domain*, le développeur doit écrire la classe ci-dessous en indiquant dans *basePackages* la liste des packages concernés par le *domain*.

```
import com.calinfo.api.common.domain.DomainDatasourceConfiguration;
import com.calinfo.api.common.domain.DomainProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(DomainProperties.CONDITIONNAL_PROPERTY)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = DomainDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF, 
                       transactionManagerRef = DomainDatasourceConfiguration.TRANSACTION_MANAGER_REF, 
                       basePackages = {...})
public class GenesisDomainDatasourceConfiguration extends DomainDatasourceConfiguration {
}
```

  * Pour indiquer les *jpaRepository* pour la base de données générique, le développeur doit écrire la classe ci-dessous en indiquant dans *basePackages* la liste des packages concernés par la base de données générique.

```
import com.calinfo.api.common.domain.DomainProperties;
import com.calinfo.api.common.domain.GenericDatasourceConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(DomainProperties.CONDITIONNAL_PROPERTY)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = GenericDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF,
    transactionManagerRef = GenericDatasourceConfiguration.TRANSACTION_MANAGER_REF,
    basePackages = {"..."})
public class GenesisGenericDatasourceConfiguration extends GenericDatasourceConfiguration {
}
```

## Comment créer un *domain* par programme ?

 La création d'un *domain* consiste à créer un schéma de base de donnée, et éventuellement jouer les scripts *liquibase*.

 Le code ci-dessous donne un exemple de comment y parvenir :

```
import com.calinfo.api.common.tenant.TenantDatasourceConfiguration;
import com.calinfo.api.common.tenant.TenantProperties;
import com.calinfo.api.common.utils.DatabaseUtils;
import com.calinfo.api.common.utils.LiquibaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MyExample {


    @Autowired
    @Qualifier(TenantDatasourceConfiguration.TENANT_DATASOURCE) // Ne pas utiliser de qualifier pour la base de données générique
    private DataSource tenantDataSource;

    @Autowired
    private TenantProperties tenantProperties;

    public void call(String domain) {

        String schemaName = TenantDatasourceConfiguration.getSchemaName(tenantProperties.getPrefix(), domain);
        DatabaseUtils.createSchema(tenantDataSource, schemaName);
        LiquibaseUtils.updateSchema(tenantDataSource, tenantProperties.getLiquibase().getChangeLog(), schemaName);

    }
}
```

## Configuration

 La configuration du domaine se fait dans la sous configuration *common.configuration.domain* du fichier *yaml* .
 Toutes les propriétés de cette sous configuration sont décrites dans la JavaDoc de la classe *com.calinfo.api.common.tenant.TenantProperties*


## Point d'attention

 Le common récupère le edomaine à utiliser (et donc le schéma) lors de l'ouverture d'une transaction (DomainContext.getDomain()).
 Si la transaction est déjà ouverte, l'utilisatiuon de DomainContext.setDomain(...) ne fonctionnera pas et peut même provoquer des bug
 incohérents.

# Realm

Le *common* sait gérer une base de données commune pour tous les domaines. Cette base de données (ou schéma) est appelé schéma générque.

Exemple de cas d'utilisation : Avoir une table commune de devises pour chaque *domain*. Inutile de dupliquer cette table dans chacun des *domain*. Il est préférable que celle-ci soit partagée.

## Comment indiquer quel est le domaine à utiliser en fonction de l'URL

Le module *common* requiert l'implémentation de la classe RealmResolver afin de déterminer quel royaume utiliser en fonction de l'URL.

Cependant, le module *common* propose une implémentation par défaut qui indique que le nom de royaume correspond au nom présent dans l'URL. Par exemple, pour l'URL http://www.calinfo-nc.com/restDeMonUrl, le nom de royaume sera www.calinfo-nc.com.

Vous avez la possibilité de personnaliser ce comportement en créant votre propre implémentation de RealmResolver.

Exemple :
```
import com.calinfo.api.common.security.keycloak.KeycloakAuthorizeHttpRequestsCustomizerConfig;
import com.calinfo.api.common.security.keycloak.RealmResolver;
import com.calinfo.api.common.tenant.Request;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import com.calinfo.api.common.security.keycloak.RealmResolver;
import org.springframework.context.annotation.Primary;

@Pimary
@Component
class MyRealmResolver implements RealmResolver {

    public String getRealm(Request request){
        return ...;
    }
}
```
