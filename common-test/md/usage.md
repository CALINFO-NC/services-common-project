
# Description

La finalité de ce module est de reporter les résultats de l'exécution des tests automatisés dans Jira à l'aide du module [XRay](https://marketplace.atlassian.com/apps/1211769/xray-test-management-for-jira?tab=overview&hosting=cloud). Il est donc nécessaire que vous vous renseignez de ce que peut faire [XRay](https://marketplace.atlassian.com/apps/1211769/xray-test-management-for-jira?tab=overview&hosting=cloud), pour comprendre la suite de ce *readme*.

# Créé des tests Java compatible Xray

Pour écrire des tests compatibles avec XRay, il faut
1. Configurer votre application
2. Ecrire votre test en Java à l'aide de TestNg
3. Consolider la liaison entre le test *Java* et le ticket *Jira*
4. Envoyer le résultat des tests dans Jira

## Configurer votre application

La configuration de votre application se fait dans votre fichier pom.xml en y ajoutant les éléments ci-dessous.

```xml
<plugin>  
	<groupId>org.apache.maven.plugins</groupId> 
	<artifactId>maven-surefire-plugin</artifactId> 
	<version>3.0.0-M3</version>  
	<configuration> 
		<testFailureIgnore>true</testFailureIgnore> 
		<properties> 
			<property> 
				<name>reporter</name>
				<value>org.testng.reporters.XMLReporter:generateTestResultAttributes=true,generateGroupsAttribute=true</value> 
			</property> 
		</properties>  
	</configuration>
</plugin> 
```  

## Ecrire votre test en Java avec TestNg

Considérez le code ci-dessous

```java 
public class WorkflowWarehouseTransfertTest extends AbstractTestNGSpringContextTests {

	@Test 
	@Xray(requirement = "CODEJIRA-1") 
	private void envoyerTransfertEntrepot() throws Exception {
		...
	}  
}  
```  

Lorsque ce test s'exécutera, le résultat de ce test créera un nouveau ticket *Jira* (par exemple *CODEJIRA-100*) qui sera associée au ticket *Jira* *CODEJIRA-1*. Le ticket Jira *CODEJIRA-100* représentera le résultat de l'exécution du test représenté par le ticket *Jira* *CODEJIRA-1*. Dans l'outil *Jira* les deux tickets *Jira*, *CODEJIRA-100* et *CODEJIRA-1*, seront liées.

## Consolider la liaison entre le test *Java* et le ticket *Jira*

A chaque exécution des tests, un nouveau ticket Jira représentant le résultat des tests est créé. Cela peut être contraignant. Afin d'éviter ce comportement il faudra indiquer dans le code de test, le numéro de la Jira créé (par exemple *CODEJIRA-100*). Pour cela il faudra utiliser la propriété *test* de l'annotation *Xray* comme ci-dessous.

```java 
public class WorkflowWarehouseTransfertTest extends AbstractTestNGSpringContextTests {  
 
	@Test 
	@Xray(requirement = "CODEJIRA-1", test = "CODEJIRA-100") 
	private void envoyerTransfertEntrepot() throws Exception { 
		... 
	}  
}  
```  

L'annotation *Xray* permet de rattacher d'un test à plusieurs tickets *Jira*. Par exemple :

```java
@Xray(requirement = {"CODEJIRA-1", "CODEJIRA-1"})
```

Cependant, XRay étant bugué pour le moment, seul le premier élément du tableau est rattaché automatiquement. Les autres éléments devront faire l'objet d'un rattachement manuel.


## Envoyer le résultat des tests dans Jira

L'exécution des tests généra un fichier *testng-results.xml*. Ce fichier devra être envoyé au serveur *Xray* pour obtenir le résultat dans l'outil *Jira*. Pour plus de détail, ce référer à la documentation officiel [https://confluence.xpand-it.com/](https://confluence.xpand-it.com/). Cependant, vous trouverez ci-dessous un exemple de script *sh* permettant d'envoyer le résultat des tests contenues dans *testng-results.xml* vers le serveur *Xray*

```bash  
#!/bin/bash  
  
clientId=5E2C649649404FCB49D10B  
clientSecret=5e83e89a2751ae31d9414b1857909982  
  
# Récupérer un token d'authentification  
xRayToken=$(curl -H "Content-Type: application/json" -X POST --data '{ "client_id": "$clientId","client_secret": "$clientSecret" }'  https://xray.cloud.xpand-it.com/api/v1/authenticate)  
xRayToken=${xRayToken#'"'}  
xRayToken=${xRayToken%'"'}  
  
testEnvironments=ENV-FACULTATIF  
projectKey=codeJira  
  
# Intégrer le résultat dans XRay  
curl -H "Content-Type: text/xml" -X POST -H "Authorization: Bearer $xRayToken"  --data @"/cheminDeMonResultat/testng-results.xml" "https://xray.cloud.xpand-it.com/api/v1/import/execution/testng?projectKey=$projectKey&testEnvironments=$testEnvironments"  
```
