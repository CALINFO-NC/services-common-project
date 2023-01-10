
# Description

Le sous-package *storage* du *common-io* offre un mécanisme de transfert des fichiers binaires d'un support à un autre en arrière-plan. Cela est surtout très pratique pour transférer les images une base de données (coûteux) vers un stockage moins coûteux (comme, par exemple, un système de fichiers, un *webdav*, un *ftp*, etc...).

# Cas d'utilisation

Nous souhaitons, par exemple, dans une application stocker des fichiers binaires dans la même transaction que ses métadonnées. Pour cela il suffit de créer une ou plusieurs tables en base de données afin de stocker les binaires et les métadonnées. Cependant stocker les données binaires en base de données est coûteux, et les stocker dans un système de fichier ne nous permet pas d'utiliser les avantages des transactions. L'idéal, serait de stocker les données binaires en base de données avec les métadonnées et, une fois la transaction *comité*, déplacer les données binaires sur un autre support. C'est le principe mis en place dans le sous-package *storage* de *coloniaux*

# Comment intégrer le méchanisme du sous-package *storage* au sein de son application ?

## Créer un service implémentant *BinaryDataClientService*

Lors de l'appel de ce service, si vous souhaitez savoir dans quel domaine les méthodes de ce service sont invoquées, vous pouvez utiliser la classe *DomainContext*.

```java  
import com.calinfo.api.common.io.storage.service.BinaryDataClientService;  
import org.springframework.stereotype.Service;  
  
@Service  
public class MyBinaryDataService implements BinaryDataClientService {  
	...
}  
```  
**Particularité d'une application multitenant**

Si votre application est une application multitenant, c’est-à-dire avec plusieurs domaines, il vous faudra implémenter le > service *BinaryDataDomainService* afin que le mécanisme du *common-io* puisse parcourir la liste des domaines.

```java  
import com.calinfo.api.common.io.storage.service.BinaryDataDomainService;  
import org.springframework.stereotype.Service;  
  
@Service  
public class MyBinaryDataDomainService implements BinaryDataDomainService {  
	...
}  
```  

## Créer un connecteur

Le *connecteur* est une interface permettant de lire et écrire les binaires d'un support (par exemple un système de fichier, un ftp, etc...). Dans votre application, la mise en place d'un connecteur se fait par l'implémentation de l'interface *BinaryDataConnector*.

```java
import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;  
import org.springframework.stereotype.Component;  
  
@Component  
public class MyBinaryDataConnector implements BinaryDataConnector {  
	...
 } 
```  

> Il existe plusieurs types de connecteur déjà implémenté dans le sous-package *storage* du *common-io*. Vous pouvez vous référer au chapitre [Connecteur](./connector.md) pour plus d'information.

## Configurer pour la prise en charge du mécanisme de transfert

C'est un *scheduler* qui déclenche le transfert des données binaires entre deux supports. Vous avez deux paramètre à mettre en place dans le fichier de configuration

* enabled: Permet d'activer le *scheduler*. Valeur possible true/false. false par défaut
* delay: Durée en milliseconde entre deux lancement du scheduler

La configuration doit être écrite dans le fichier de propriété (par exemple le fichier *application.yml*). L'exemple ci-dessous montre comment l'écrire dans le fichier *application.yml*

```yaml
common-io:  
	storage: 
	scheduler: 
		enabled: true		# true pour activer le transfert de donnée binaire 
		delay: 60000		# Délai en milliseconde entre chaque tentative de transfert  
  
```  

Le mode de fonctionnement du *scheduler* consiste à lancer un service annoté *@Async* pour chaque domaine sur lequel effectuer un transfert de données binaires. En d’autres termes, un thread effectuant le transfert est déclenché pour chaque *domaine*. En réalité le nombre de *thread* est égal au nombre de *domaine + 1*. Car un thread est déclenché aussi pour une valeur de *domaine = null*. Si le mode *asynchrone* n'est pas activé dans votre application, alors la synchronisation des *domaines* sera séquentielle.

> IMPORTANT : Si vous activez la synchronisation dans votre application gardez à l'esprit que le *scheduler* estime sa tâche terminée quand il a déclenché la méthode annotée par @Async, et non quand cette dernière est terminée.

Afin de limiter le nombre de thread déclenché, il est fortement recommandé de créer un *bean* de type *TaskExecutor* et nommé *binaryDataASyncOperation*. Ci-dessous un exemple permettant de code permettant de créer ce *bean*.

```java
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.core.task.TaskExecutor;  
import org.springframework.scheduling.annotation.EnableAsync;  
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;  
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;  
  
@Configuration  
@EnableAsync // Exemple d'activation de la synchronisation dans votre application 
public class ThreadConfig {  
  
    private static final int HEURE = 60 * 60;  
  
    @Bean(name = "binaryDataASyncOperation")   
    public TaskExecutor binaryDataASyncOperation() {   
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
