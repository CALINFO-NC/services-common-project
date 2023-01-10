
# Description

Ce chapitre traite des façons d'exécuter du code en changeant les éléments de contexte suivants…
* L'utilisateur connecté
* Les rôles de l'utilisateur connecté
* Le domaine

# Usage

Afin que la tâche Asynchrone connaisse le contexte du domaine, il faudra créer *bean* qui renvoie un *Executor* de *spring*. L'implémentation du *Executor* devra être celle de la classe *DomainAwarePoolExecutor*

Exemple de code :

```java  
import com.calinfo.api.common.tenant.DomainAwarePoolExecutor;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;  
import org.springframework.scheduling.annotation.EnableAsync;  
  
import java.util.concurrent.Executor;  
  
@Configuration  
@EnableAsync  
public class ExecutorConfig extends AsyncConfigurerSupport {  
  
	@Override 
	@Bean public Executor getAsyncExecutor() { 
		return new DomainAwarePoolExecutor(); 
	}
}  
```
