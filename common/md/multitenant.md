# Description

 La gestion *multi-tenant* implémentée par le *common* est un mécanisme permettant, pour une seule instance d'une application, d'avoir plusieurs schémas d'une même structure de base de données.
 Dans le reste du *common*, nous avons appelé *domain* une représentation de ce schéma. Ajouter à cela, le *common* sait gérer une base de données commune pour tous les domaines.
 Exemple de cas d'utilisation : Avoir une table commune de devises pour chaque *domain*. Inutile de dupliquer cette table dans chacun des *domain*. Il est préférable que celle-ci soit partagée.

 * Le *domain* peut être défini lors de l'authentification de l'utilsiateur :
 En effet, lors de l'authentification de l'utilisateur, le *token* peut véhiculer le *domain* auquel l'utilisateur s'authentifie. Ce *domain* peut être
 ensuite utilisé par le développeur en appelant la méthode *getDomain()* d'une instance de *AbstractCommonPrincipal* (Voir la sécurité pour plus de détails).

 * Le *domain* peut être défini par le développeur lors de l'appel d'une requête HTTP :
 Pour cela, le développeur doit indiquer lors de l'appel d'une requête HTTP, quel est le *domain* souhaité. Pour se faire, le développeur
 indiquer dans le context via *com.calinfo.api.common.tenant.DomainContext.setDomain(...)* le domain souhaité.

```
Exemple :

@Component
public class MyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String oldDOmain = DomainContext.getDomain();
        try{
            String domainName = ...;

            DomainContext.setDomain(domainName);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        finally{
            DomainContext.setDomain(oldDOmain);
        }
    }
}
```

# Comment le *common* sait-il quels sont les *repository* et les *entity* utilisés pour un *domain* et ceux utilisés pour la base de données commune (ou générique) ?

 Le développeur doit indiquer ces informations au *common*.

  * Pour indiquer les *entity* pour le *domain* le développeur doit, dans le fichier *yaml*, définir la configuration *common.configuration.domain.domainScanEntities*

  * Pour indiquer les *entity* pour la base de données générique, le développeur doit, dans le fichier *yaml*, définir la configuration *common.configuration.domain.genericScanEntities*.

  * Pour indiquer les *jpaRepository* pour le *domain*, le développeur doit écrire la classe ci-dessous en indiquant dans *basePackages* la liste des packages concernés par le *domain*.

```
import com.calinfo.api.common.tenant.TenantDatasourceConfiguration;
import com.calinfo.api.common.tenant.TenantProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = TenantDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF,
        transactionManagerRef = TenantDatasourceConfiguration.TRANSACTION_MANAGER_REF,
        basePackages = {...}
)
public class DomainDatasourceConfiguration extends TenantDatasourceConfiguration {
}
```

  * Pour indiquer les *jpaRepository* pour la base de données générique, le développeur doit écrire la classe ci-dessous en indiquant dans *basePackages* la liste des packages concernés par la base de données générique.

```
import com.calinfo.api.common.tenant.DefaultDatasourceConfiguration;
import com.calinfo.api.common.tenant.TenantProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = DefaultDatasourceConfiguration.ENTITY_MANAGER_FACTORY_REF,
        transactionManagerRef = DefaultDatasourceConfiguration.TRANSACTION_MANAGER_REF,
        basePackages = {...}
)
public class GenericDatasourceConfiguration extends DefaultDatasourceConfiguration {
}
```

# Comment créer un *domain* par programme ?

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

# Configuration

 La configuration du *tenant* se fait dans la sous configuration *common.configuration.domain* du fichier *yaml* .
 Toutes les propriétés de cette sous configuration sont décrites dans la JavaDoc de la classe *com.calinfo.api.common.tenant.TenantProperties*


# Point d'attention

 Le common récupère le *domain* à utiliser (et donc le schéma) lors de l'ouverture d'une transaction (DomainContext.getDomain()).
 Si vous la transaction est déjà ouverte, l'utilisatiuon de DomainContext.setDomain(...) ne fonctionnera pas et peut même provoquer des bug
 incohérents.
