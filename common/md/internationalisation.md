
# Description

Le *common* impose un mécanisme d'internationalisation. En d'autres termes, une application ne doit **JAMAIS** avoir en son sein des phrases dans une langue humaine.

>Exemple : lorsqu'un paramètre est obligatoire, le développeur ne doit pas envoyer l'erreur "Paramètre obligatoire".  La bonne pratique indique, que le développeur doit envoyer un code correspondant à l'erreur (par exemple *PARAM_MADATORY*)

Pour cela le *common* impose l'implémentation du service *com.calinfo.api.common.service.MessageService* ayant une méthode *translate*. *translate* a pour paramètre la *Local*, le code de l'erreur et les paramètres associés aux codes. Cette méthode a pour objectif de transformer le code erreur avec ses paramètres éventuels en langage humain correspondant à la langue définie par la *Local*.

# Comment envoyer un message au *front* depuis le *back* ?

Il existe plusieurs types de messages.
* Les *informations*
* Les *warnings*
* Les *erreurs*

## Les messages de type *informations* et *warning*

Les *informations* et les *warnings* ne provoquent pas l'arrêt du traitement en cours. C'est pour cela qu'elles sont représentées comme des propriétés de la classe résultat. Dans le cas des DTOs ou des ressources, ces propriétés sont :
* listWarningMessages
* listInfoMessages

Afin d'harmoniser le code, si vous souhaitez renvoyer des messages de type *information* ou *warning* via un DTO ou une ressource, faites hériter ce DTO ou cette ressource par *com.calinfo.api.common.dto.MessageInfoAndWarningInterface*.

Exemple :
 ```java
 import com.calinfo.api.common.dto.DefaultMessageInfoAndWarning;  
 import lombok.AccessLevel;  
 import lombok.Getter;  
 import lombok.Setter;  
 import lombok.experimental.Delegate;  
   
 public class MonDto implements MessageInfoAndWarningInterface {  
   
     @Getter(AccessLevel.PRIVATE)  
     @Setter(AccessLevel.PRIVATE)  
     @Schema(hidden = true)  
     @Delegate  
     private DefaultMessageInfoAndWarning messageInfoAndWarning = new DefaultMessageInfoAndWarning();  
 }
```

## Les messages de type *erreur*

Les erreurs, en revanche, provoquent l'arrêt du traitement en cours. C'est pour cela, qu'elles sont déclenchées en levant une exception. Les erreurs remontées au *front* sont soit des erreurs sur un champ particulier d'un DTO ou d'une ressource, soit des erreurs globales (ou les deux).

### Envoyer une erreur globale

```java
ServiceErrorStructure serviceErrorStructure = new ServiceErrorStructure();  
serviceErrorStructure.getGlobalErrors().add(new MessageStructure(MessageCode.CODE_ERREUR_GLOBAL1), new MessageStructure(MessageCode.CODE_ERREUR_GLOBAL2));  
throw new MessageException(serviceErrorStructure);  
```  

### Envoyer une erreur sur un champ particulier

```java 
FieldErrorStructure fieldError = new FieldErrorStructure();  
fieldError.put("resource.captcha.value", new MessageStructure(MessageCode.CODE_ERREUR_FIELD1), new MessageStructure(MessageCode.CODE_ERREUR_FIELD2));  
fieldError.put("resource.captcha.key", new MessageStructure(MessageCode.CODE_ERREUR_FIELD3), new MessageStructure(MessageCode.CODE_ERREUR_FIELD4));  
ServiceErrorStructure serviceErrorStructure = new ServiceErrorStructure();  
serviceErrorStructure.setFieldsErrors(fieldError);  
throw new MessageException(serviceErrorStructure);  
```  

### Envoyer une erreur sur un champ particulier + une erreur globale

```java  
ServiceErrorStructure serviceErrorStructure = new ServiceErrorStructure();  
  
FieldErrorStructure fieldError = new FieldErrorStructure();  
fieldError.put("resource.captcha.value", new MessageStructure(MessageCode.CODE_ERREUR_FIELD));  
serviceErrorStructure.setFieldsErrors(fieldError);  
  
serviceErrorStructure.getGlobalErrors().add(new MessageStructure(MessageCode.CODE_ERREUR_GLOBAL));  
  
throw new MessageException(serviceErrorStructure);  
```
