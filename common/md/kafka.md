# Description

 Le *common* propose une intégration d'apache [Kafka](https://kafka.apache.org/)

 L'intégration est basée sur [Spring Kafka](https://spring.io/projects/spring-kafka)

 L'integration de Kafka au seins du common consiste à transférer en tant que message Kafka, une instance de la classe *com.calinfo.api.common.kafka.KafkaEvent*

# Comment envoyer un topic à Kafka avec comme objet *KafkaEvent* ?

```
class MaClass {

   @KafkaTopic(value = "nomTopic", kafkaPrefixeMandatory = false)
   public MyResource myMethod(...){
      ...
   }
}
```

Il possible aussi de nommé un topic comme ceci

```
abstract class SuperClass {

    @KafkaTopic("nomTopic")
    public MyResource myMethod(...){
    ...
    }
}

@KafkaPrefixTopic("MonPrefix") 
class SubClass extends SuperClass {
    
}
```

Dans ce cas le topic aura pour nom **MonPrefix.nomTopic**

# Comment écouter un topic à Kafka avec comme objet *KafkaEvent* ?

```
 @KafkaListener(topics = "idApplicationEmetteurTopic---domain---nomTopic")
 public void receiveTopic(KafkaEvent kafkaEvent) {
    ...
 }

 OU, s'il n'y a pas de domain

@KafkaListener(topics = "idApplicationEmetteurTopic------nomTopic")
public void receiveTopic(KafkaEvent kafkaEvent) {
 ...
}
```

# Comment utiliser *KafkaEvent* ?

 La méthode *get* de *KafkaEvent* vous permet de récupérer une instance du retour de la méthode
 sur laquelle *@KafkaTopic* a été posée. Dans l'exemple ci-dessus, la méthode *KafkaEvent.get* retournerait
 une intance de *MyResource*.

 Dans le cas ou la méthode sur laquelle  *@KafkaTopic* a été apposée retourne une exception sans
 de Rollback alors *KafkaEvent.get* lève une *KafkaException* dont la cause est

 * Soit l'exception levée par la méthode sur laquelle *@KafkaTopic* a été posée. Dans ce cas l'exception levée est
 *KafkaInvocationException* qui est une sous classe de *KafkaException*

 * Soit la cause original n'ayant pas permis de reconsituer l'exception sur laquelle *@KafkaTopic* a été posée.
 Dans ce cas l'exception levée est *KafkaRestitutionException* qui est une sous classe de *KafkaException*

 Dans les deux cas, il est possible de récupérer le nom original de l'exception levée par la méthode sur laquelle *@KafkaTopic*
 a été posée en utilisant la méthode *KafkaException.getOriginalCauseClassName*

 Dans le cas ou la méthode sur laquelle  *@KafkaTopic* a été apposée retourne une exception
 faisant un Rollback alors aucun message Kafka est envoyé

# Configuration

 Par défaut l'annotation *@KafkaTopic* n'est pas activé. Il est possible de l'activer en ajoutant dans le fichier de
 configuration *application.yml* les lignes suivantes :

```
common:
  configuration:
    kafka-event:
      enabled: true
```

 Le reste de la configuration est de la configuration standard kafka-spring.
 Ci-dessous un exemple de configuration

 NB : Ne pas oublier d'activer Les écouteur Kafka avec l'annotation *@EnableKafka*

```
spring:
  kafka:
    bootstrap-servers: ${spring.embedded.kafka.brokers}
    listener:
      missing-topics-fatal: false   # Cette configuration est tr!ès importante car le common créé les topic à la volet
    consumer:
      auto-offset-reset: earliest
      group-id: ${common.configuration.application.id}
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: com.calinfo.api.common.kafka
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

 Notez le *JsonDeserializer* et le *JsonSerializer*

 ATTENTION : Dans le cas ou vous utilisez l'annotation *@KafkaTopic* il est fortement conseillé d'activer l'exécution
 des méthodes asynchrones, via l'annotation spring *@EnableAsync(mode = AdviceMode.ASPECTJ)* (sur une configuration par exemple).

# Comment récupérer *KafkaEvent* Sans mettre en place Kafka

 Il est possible de récupérer les évènements envoyer à Kafka et les traiter manuellement au lieu de les envoyer à Kafka.
 Pour cela vous n'avez pas besoins de la configuration *spring.kafka...*. En revanche, il faudra la configuration
 *common.configuration.kafka.event...* et particulièrement mettre à false cette configuration *common.configuration.kafka-event.kafka-listener-enabled*

 Si vous voulez intercepter un évènement Kafka, il vous suffit d'écrire le code ci-dessous

```
@Async  // Pas obligatoire mais vivement conseillé
@TransactionalEventListener(...)
public void call(KafkaEvent kafkaEvent) {

    ...
}

Soit

@Async  // Pas obligatoire mais vivement conseillé
@EventListener(...)
public void call(KafkaEvent kafkaEvent) {

    ...
}
```

