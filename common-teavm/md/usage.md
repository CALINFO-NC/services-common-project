# Ecrire du code javascript en java

Ce module se base sur la bibliothèque [TeaVM](http://teavm.org/) afin de pouvoir écrire du code JavaScript en Java.  
Pour bien comprendre le besoin couvert par *common-teavm*, imaginez le cas de figure suivant.
Vous avez un contrôle de mot de passe à effectuer (par exemple minimum 8 caractères). Il est important que ce contrôle soit fait sur le back (côté java), et il serait judicieux que ce même contrôle soit effectué coté front (côté javascript). Avec la bibliothèque *common-teavm*, vous écrivez ce contrôle uniquement en Java, et le code est automatiquement traduit dans une fonction une *javascript*. Ainsi vous pouvez profiter du code à la fois sur le back et le front en ne l'écrivant qu'une fois.

# Mise en oeuvre

Pour écrire du code java traduit en javascript, il faut suivre les étapes suivantes :

## Configurer votre pom.xml comme ceci

```xml
<plugins>  

	<!-- Générateur du fichier javascript TeaVM --> 
	<plugin>
		<groupId>org.teavm</groupId>
		<artifactId>teavm-maven-plugin</artifactId>
		<version>${version.teavm}</version> 
		
		<!-- propriétée hérité du common -->
		<executions>
			<execution>
				<id>common-teavm</id>
				<goals>
					<goal>compile</goal>
				</goals>
				<phase>compile</phase>
				<configuration>

					<!-- Lieu de stockage du fichier javascript généré --> 
					<targetDirectory>${project.build.directory}/classes/static/common-teavm</targetDirectory>      

					<!-- Classe générée par le common-teavm. Il est important de laisser cette valeur -->  
					<mainClass>com.calinfo.teavm.JsClass</mainClass>                     

					<!-- minifié le fichier javascript généré -->  
					<minifying>true</minifying>
				</configuration>
			</execution> 
		</executions> 
	</plugin>
	...  
</plugins>  
  
<dependencies>
	<!-- Processor permetant de générer la classe com.calinfo.teavm.JsClass pour TeaVM -->  
	<dependency>
		<artifactId>common-teavm</artifactId>
		<groupId>com.calinfo-nc.services</groupId> 
		<version>1.0.1-SNAPSHOT</version> 
	</dependency>
	...
</dependencies>
```  

## Configurer votre page HTML comme ceci

```html
<head>  
    ...  
	<script type="text/javascript" charset="utf-8" src="/common-teavm/classes.js"></script>  
	<script  type="text/javascript">main();</script>  
	...  
</head>  
```  

## Ecrire le code java

Explication par l'exemple. Considerez le code *java* ci-dessous :

```java
import com.calinfo.api.common.teavm.annotations.JsClass;  
import com.calinfo.api.common.teavm.annotations.JsMethod;  

@JsClass(namespace = "MonNameSpace")  
public class MyJsClass {  
	@JsMethod(name = "maMethod") 
	static int calculateSize(String[] tab){ return tab.length; }  
}  
``` 

Le code *java* ci-dessus aura pour effet d'écrire la fonction *javascript* correspondante et accessible depuis *document.$server.MonNameSapce.maMethod(...)*.

> Attention en intégrant *common-teavm*, cela vous génère une nouvelle classe avec un *main*. Le démarrage de votre application *springboot* risque de ne plus fonctionner. Vous devez indiquer dans votre configuration quelle est la vrai classe *main* à utiliser par *springboot*. Vous devriez avoir quelque chose comme ceci dans votre *pom.xml* :
>
> ```xml
> <plugin>
> 	<groupId>org.springframework.boot</groupId>
> 	<artifactId>spring-boot-maven-plugin</artifactId>
> 	<version>${version.spring}</version>
> 	<executions>
> 		<execution>
> 			<goals>
> 				<goal>repackage</goal>
> 			</goals>
> 		</execution>
> 	</executions>
> 	<configuration>
> 		<mainClass>com.calinfo.api.Application</mainClass>
> 	</configuration>
> </plugin>  
> ```  

# Contrainte Java

La bibliothèque TeaVM demande cependant de respecter quelques contraintes lors de l'écriture de la class Java

* Le constructeur de la classe ne doit comporter aucun paramètre
* La classe ne peut pas utiliser l'injection
* Les méthodes annotées avec *JsMethod* ne peuvent pas renvoyer d'exception. Cependant le *RuntimeException* (ou dérivé) est géré.
* Les méthodes annotées avec *JsMethod* doivent être *static*.
* Les types gérés en paramètre ou en retour sont *void*, *int* (le *long* n'est pas géré), *double*, *float*, *short*, *byte*,  
  *boolean*, *java.lang.String*, ainsi que les tableaux ([] et non des listes) de tous ces types.

# Configuration avancée

* Vous pouvez changer le nom du package et de la class Main de TeaVM en compilant votre programme avec les propriétés systèmes *calinfo.common.teavm.main.packageName* et *calinfo.common.teavm.main.className"

Exemple :

```      
mvn clean install -Dcalinfo.common.teavm.main.packageName=pkg,calinfo.common.teavm.main.className=MaClass  
```
La commande ci-dessus généra la classe *pkg.MaClass* et de ce fait dans votre pom.xml, il faudra référencer ceci :

```xml
<mainClass>pkg.MaClass</mainClass>
```
Au lieu de :

```xml
<mainClass>com.calinfo.teavm.JsClass</mainClass>  
 ```

* Vous pouvez modifier la racine du *namespace* en modifiant la propriété système *calinfo.common.teavm.main.rootNameSpace*

Exemple :

```
mvn clean install -Dcalinfo.common.teavm.main.rootNameSpace=document.$root  
```

Avec la commande ci-dessus, vous pourrez accéder aux fonctions javascript à partir de *document.$root* au lieu de *document.\$server*

* Vous pouvez avoir plus de *log* sur la compilation en mettant *trace* dans la propriété système *calinfo.common.teavm.log.level*
