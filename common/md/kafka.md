
# Description

Le *common* propose une intégration d'apache [Kafka](https://kafka.apache.org/). L'intégration est basée sur [Spring Kafka](https://spring.io/projects/spring-kafka)

L'intégration de *Kafka* aux seins du *common* consiste à transférer en tant que message *Kafka*, une instance de la classe *com.calinfo.api.common.kafka.KafkaEvent*. Cela permet d'harmoniser la structure des messages gérés dans *Kafka*. Ainsi tous les messages envoyés par le *common* dans *Kafka* ont la même structure. La structure du message est la "*jsonification*" de la classe *com.calinfo.api.common.kafka.KafkaEvent*.

De manière générale le *common* peut envoyer un message *Kafka* lors de n'importe quel appel d'un service. Ainsi le mécanisme proposé par le *common* est d'offrir la possibilité au développeur d'indiquer qu'a l'appel d'un service, un message *Kafka* contenant les métadonnées représentant la structure du service appelé (par ex le nom de la classe, le nom de la méthode, le type des paramètres etc...), ainsi que les valeurs des paramètres d'entrées et de sortie, soit automatiquement envoyé dans *Kafka*


# Comment envoyer un topic à *Kafka* avec comme objet *KafkaEvent* ?

```java  
class MaClass {  

	@KafkaTopic(value = "nomTopic", kafkaPrefixeMandatory = false) 
	public MyResource myMethod(...){
		...
	}
}  
```  
Le code ci-dessus enverra un topic du nom de "*nomTopic*" à la plateforme *Kafka*. Dans certain cas, nous voulons distinguer le nom du topic *Kafka* entre classes héritées. Pour cela vous pouvez utiliser l'annotation *KafkaPrefixTopic* comme ceci :

```java
abstract class SuperClass {  
  
	@KafkaTopic("nomTopic") 
	public MyResource myMethod(...){ 
		... 
	}
}  
  
@KafkaPrefixTopic("MonPrefix") 
class SubClass extends SuperClass {  
	...
}  
```  
Le code ci-dessus enverra un topic du nom de "*MonPrefix.nomTopic*" à la plateforme *Kafka*

# Comment écouter un topic à *Kafka* avec comme objet *KafkaEvent* ?

```java  
@KafkaListener(topics = "idApplicationEmetteurTopic---domain---nomTopic") 
public void receiveTopic(KafkaEvent kafkaEvent) { 
	...
}  
```  
OU, s'il n'y a pas de domaine (c’est-à-dire que ce n'est pas une application multiTenant),

```java  
@KafkaListener(topics = "idApplicationEmetteurTopic------nomTopic")  
public void receiveTopic(KafkaEvent kafkaEvent) {  
	...
}  
```  

# Comprendre les noms de topic

Les noms des topics sont constitués comme ceci :

> idApplicationEmetteurTopic---domainApplicationEmetteurTopic---prefixAnnoteAvecKafkaPrefixTopic.nomTopicAnnoteAvecKafkaTopic

Si l'application émettrice du topic n'est pas une application multitenant, alors les noms de topics sont de la forme :

> idApplicationEmetteurTopic------prefixAnnoteAvecKafkaPrefixTopic.nomTopicAnnoteAvecKafkaTopic

Si l'annotation *KafkaPrefixTopic* n'a pas été utilisé, alors les noms de topics sont de la forme :
> idApplicationEmetteurTopic---domainApplicationEmetteurTopic---nomTopicAnnoteAvecKafkaTopic

Si la valeur *kafkaPrefixeMandatory* de l'annotation *KafkaTopic* est à *false*, alors les noms de topics sont de la forme :
> prefixAnnoteAvecKafkaPrefixTopic.nomTopicAnnoteAvecKafkaTopic

Si la valeur *kafkaPrefixeMandatory* de l'annotation *KafkaTopic* est à *true* et que la valeur *prefixTopicNameWithApplicationId* de l'annotation *KafkaTopic* est à *false*, alors les noms de topics sont de la forme :
> ---domainApplicationEmetteurTopic---prefixAnnoteAvecKafkaPrefixTopic.nomTopicAnnoteAvecKafkaTopic

Si la valeur *kafkaPrefixeMandatory* de l'annotation *KafkaTopic* est à *true* et que la valeur *prefixTopicNameWithDomain* de l'annotation *KafkaTopic* est à *false*, alors les noms de topics sont de la forme :
> idApplicationEmetteurTopic------prefixAnnoteAvecKafkaPrefixTopic.nomTopicAnnoteAvecKafkaTopic

Si la valeur *kafkaPrefixeMandatory* de l'annotation *KafkaTopic* est à *true* et que les valeurs *prefixTopicNameWithDomain* et *prefixTopicNameWithApplicationId* de l'annotation *KafkaTopic* sont à *false*, alors les noms de topics sont de la forme :
> ------prefixAnnoteAvecKafkaPrefixTopic.nomTopicAnnoteAvecKafkaTopic

# Mettre en place la configuration nécessaire

Par défaut l'annotation *@KafkaTopic* n'est pas activé. Il est possible de l'activer en ajoutant dans le fichier de configuration *application.yml* les lignes suivantes :

```yaml  
common:  
	configuration: 
		kafka-event: 
			enabled: true
```  

Le reste de la configuration, est de la configuration standard *kafka-spring*. Ci-dessous un exemple de configuration

> NB : Ne pas oublier d'activer Les écouteur Kafka avec l'annotation *@EnableKafka*

```yaml
spring:  
	kafka: 
		bootstrap-servers: ${spring.embedded.kafka.brokers} 
		listener: 
			missing-topics-fatal: false   # Cette configuration est très importante car le common créé les topic à la volet 
		consumer: 
			auto-offset-reset: earliest 
			group-id: ${common.configuration.application.id} 
			value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer 
			properties: 
				spring.json.trusted.packages: com.calinfo.api.common.kafka 
		producer: 
			value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```  

> Notez le *JsonDeserializer* et le *JsonSerializer*

> ATTENTION : Dans le cas où vous utilisez l'annotation *@KafkaTopic* il est fortement conseillé d'activer l'exécution des méthodes asynchrones, via l'annotation spring *@EnableAsync(mode = AdviceMode.ASPECTJ)* (sur une configuration par exemple).

# Comment traiter manuellement les messages *KafkaEvent*

Vous pouvez avoir le besoin d'intercepter les évènements qui aurait dû être envoyé à Kafka afin de faire un traitement manuel. Pour cela, vous devrez définir la configuration *common.configuration.kafka-event.kafka-listener-enabled* afin de lui affecter la valeur *false" et écrire le code suivant :

```java  
@Async  // Pas obligatoire mais vivement conseillé  
@TransactionalEventListener(...)  
public void call(KafkaEvent kafkaEvent) {  
	...
}  
```
Ou encore :

```java    
@Async  // Pas obligatoire mais vivement conseillé  
@EventListener(...)  
public void call(KafkaEvent kafkaEvent) {  
	...
}  
```
Les deux exemples de code ci-dessus, vous permettent d'intercepter les évènements *KafkaEvent* envoyé par le *common* afin de faire un traitement manuel.

> En fonction de votre traitement, la configuration standard *kafka-spring*, n'est pas forcément utile.
