# Description

 Ce chapitre traite de la façons dont appeler une tâche asynchrone en passant le Domain en paramètre.

# Usage

 Afin que la tâche Asynchrone connaisse le context du domaine, il faudra crééer bean qui renvoie un *Executor* de spring.
 L'impléméntation du *Executor* devrat être celle de la class *TenantAwarePoolExecutor*

Exemple de code :

```java
import com.calinfo.api.common.tenant.TenantAwarePoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ExecutorConfig extends AsyncConfigurerSupport {

 @Override
 @Bean
 public Executor getAsyncExecutor() {
  return new TenantAwarePoolExecutor();
 }
}
```
