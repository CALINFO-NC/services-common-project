Description

 Le sous package <storage> du <common-io> offre un méchanisme de trasfert des fichiers binaires d'un support à un autre en arrière plan.
 Cela est surtout très pratique pour transférer les images d'une base de données (couteux) vers un stockage moins couteux
 comme <google cloud> par exemple.

Cas d'utilisation

 Nous souhaitons, par exemple, dans une application stocker des fichiers binaires dans la même transaction que ses méta-données.
 Pour cela il suffit de créer une ou plusieurs tables en base de données afin de stocker les binaires et les méta-données.
 Cependant stocker les données binaires en base de données est couteux, et les stocker dans un <file system> ne nous permet pas d'utiliser les avantages des transactions.
 L'idéal, serait de stocker les données binaires en base de données avec les métadonnés et une fois la transaction commitée, de déplacer les données
 binaires vers un autre support. C'est le principe mis en place dans le sous package <storage> de <common-io>

Comment intégrer le méchanisme du sous package <storage> au sein de son application ?

 * Créer un service implémentant <BinaryDataClientService>

  Lors de l'appel de ce service, si vous souhaitez savoir dans quel domaine les méthodes de ce service sont invoquées, vous pouvez utiliser la classe <DomainContext>.

```
import com.calinfo.api.common.io.storage.service.BinaryDataClientService;
import org.springframework.stereotype.Service;

@Service
public class MyBinaryDataService implements BinaryDataClientService {

    ...
}
```

 * Multi tenant

 Si votre application est une applciation multi tenant, c'est à dire avec plusieurs domaines, il vous faudra implémenter le service <BinaryDataDomainService>.

```
import com.calinfo.api.common.io.storage.service.BinaryDataDomainService;
import org.springframework.stereotype.Service;

@Service
public class MyBinaryDataDomainService implements BinaryDataDomainService {

    ...
}

```

 * Connecteur

 Ensuite le sous package <storage> de <common-io> a besoins de savoir comment lire et écrire des fichiers sur un autre support.
 Dans ce cas, il vous faudra implémenter le composant <BinaryDataConnector>.

```
import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import org.springframework.stereotype.Component;

@Component
public class MyBinaryDataConnector implements BinaryDataConnector {

    ...
}

```

 Il existe plusieurs type de connecteur déjà implémenté dans le sous package <storage> du <common-io>. Voir chapitre <Connecteur>.


 * Enfin la configuration nécessaire pour mettre en oeuvre de méchanisme du sous package <storage> de <common-io>

 Ajouter les lignes ci-dessous dans le fichier de propriété (ex : application.yml)

```
common-io:
  storage:
    scheduler:
      enabled: true     # true pour activer le transfert de donnée binaire
      delay: 60000      # Délai en mili seconde entre chaque tentative de transfert


```

 * Multi-threading

 ATTENTION : chaque transfert de fichier est lancé dans un thread à part. Il est fortement recommandé de maitriser le nombre de Thread
 lancé simultanément. pour cela vous devrez créer un bean nommé <"binaryDataASyncOperation"> de type <org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor>

 Ci-dessous un exemple d'implémentation

```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
public class ThreadConfig {

    private static final int HEURE = 60 * 60;

    @Bean(name = "binaryDataASyncOperation")
    public TaskExecutor saveEsActionThreadPoolExecutor() {
        return createExecutor(1, 12 * HEURE, "binaryDataASyncOperation_");
    }

    private TaskExecutor createExecutor(int corePoolSize, int keepAliveSeconds, String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}


```