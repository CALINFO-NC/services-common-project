# Description

Le *common* n'apporte pas de code supplémentaire pour les logs. Cependant, l'infra CALINFO utilise *[LogZ.io](https://logz.io/)* pour traiter ses logs. Pour envoyer les logs de votre application vers *[LogZ.io](https://logz.io/)* il vous faut configurer *logback-spring.xml* comme ceci :

```xml  
<?xml version="1.0" encoding="UTF-8"?>  
	<configuration>  
		<variable name="LOG_LEVEL_ROOT" value="${LOG_LEVEL_ROOT:-warn}" /> 
		<variable name="LOG_LEVEL_APP" value="${LOG_LEVEL_APP:-warn}" /> 
		<variable name="LOG_LEVEL_HIBERNATE" value="${LOG_LEVEL_HIBERNATE:-warn}" />  
		
		<springProfile name="logzio"> 
			<property name="appender" value="LOGZIO" /> 
		</springProfile>  
		
		<springProfile name="logzio"> 

			<!-- Use shutdownHook so that we can close gracefully and finish the log drain --> 	
			<shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/> 
				<appender name="LOGZIO" class="io.logz.logback.LogzioLogbackAppender"> 
					<token>${LOGZIO_API_KEY}</token> 
					<logzioUrl>${LOGZIO_URL}</logzioUrl> 
					<logzioType>java</logzioType> 
					<additionalFields>applicationName=**NomApplication**;environment=${ENVIRONMENT}</additionalFields> 
					<addHostname>true</addHostname> 
					<filter class="ch.qos.logback.classic.filter.ThresholdFilter"> 
						<level>INFO</level> 
					</filter> 
				</appender> 
			</springProfile>  
			
	<root level="${LOG_LEVEL_ROOT}"> 
		 <appender-ref ref="${appender}" /> 
	</root>  
	<logger name="com.calinfo.api" level="${LOG_LEVEL_APP}" additivity="false"> 
		<appender-ref ref="${appender}" /> 
	</logger>  
 
	<logger name="org.hibernate" level="${LOG_LEVEL_HIBERNATE}" additivity="false"> 
		 <appender-ref ref="${appender}" /> 
	</logger>
</configuration>  
```  

Si le développeur souhaite afiner son niveau de log, il peut définir les variables d'environnements ci-dessous
* LOG_LEVEL_ROOT: Niveau de log général (warn par défaut)
* LOG_LEVEL_APP: Niveau de log de l'application (warn par défaut)
* LOG_LEVEL_HIBERNATE: Niveau de log Hibernate (warn par défaut)

Ci-dessous un exemple de fichier *logback-spring.xml* avec 2 *appenders*. Un sur LOGZIO et l'autre sur la console du développeur.

```xml  
<?xml version="1.0" encoding="UTF-8"?>  
<configuration>  
	<variable name="LOG_LEVEL_ROOT" value="${LOG_LEVEL_ROOT:-warn}" /> 
	<variable name="LOG_LEVEL_APP" value="${LOG_LEVEL_APP:-warn}" /> 
	<variable name="LOG_LEVEL_HIBERNATE" value="${LOG_LEVEL_HIBERNATE:-warn}" />  
	<springProfile name="logzio"> 
		<property name="appender" value="LOGZIO" /> 
	</springProfile>  
	<springProfile name="!logzio"> 
		<property name="appender" value="CONSOLE" /> 
	</springProfile>  
 
	<!-- Use shutdownHook so that we can close gracefully and finish the log drain --> 
	<shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/> 
	<appender name="LOGZIO" class="io.logz.logback.LogzioLogbackAppender"> 
		<token>${LOGZIO_API_KEY}</token> 
		<logzioUrl>${LOGZIO_URL}</logzioUrl> 
		<logzioType>java</logzioType> 
		<additionalFields>applicationName=**NomApplication**;environment=${ENVIRONMENT}</additionalFields> 
		<addHostname>true</addHostname> 
		<filter class="ch.qos.logback.classic.filter.ThresholdFilter"> 
			<level>INFO</level> 
		</filter> 
	</appender>  
 
	 <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender"> 
		 <layout class="ch.qos.logback.classic.PatternLayout"> 
			 <Pattern> 
				 %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable 
			</Pattern> 
		</layout> 
	</appender>  
	
	<root level="${LOG_LEVEL_ROOT}"> 
		<appender-ref ref="${appender}" /> 
	</root>  
	
	<logger name="com.calinfo.api" level="${LOG_LEVEL_APP}" additivity="false"> 
		<appender-ref ref="${appender}" /> 
	</logger>  
	
	<logger name="org.hibernate" level="${LOG_LEVEL_HIBERNATE}" additivity="false"> 
		<appender-ref ref="${appender}" /> 
	</logger>  
</configuration>  
```
> N'oubliez pas de remplacer **NomApplication** 
