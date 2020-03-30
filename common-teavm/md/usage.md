# Ecrire du code javascript en java

 Ce module ce base sur la bibliothèque [TeaVM](http://teavm.org/) afin de pouvoir écrire du code JavaScript en Java.
 Afin de bien comprendre le besoin couvert par *common-teavm*, imaginez le cas de figure suivant :
 Vous avez un contrôle de mot de passer à effectuer (par exemple minimum 8 caractères). Il est important que ce contrôle
 soit fait sur le back, surtout s'il fait suite à l'appel d'un service RTEST. Pour éviter des aller/retour avec le serveur,
 il serait aussi, judicieux de le faire côté front.
 
 Avec la bibl common-teavm, vous écrivez ce contrôle en Java, et le code est automatiquement traduit dans une fonction
 javascript. Ainsi vous pouvez profiter du code à la fois sur le back et le front en ne l'écrivant qu'une fois.
 
# Mise en oeuvre

Pour écrire du code déportable en javascript, cela est asser simple. Il suffit de suivra les étapes suivantes :

* Configurer votre pom.xml comme ceci
```
<plugins>

    <!-- Générateur du fichier javascript TeaVM -->
    <plugin>
        <groupId>org.teavm</groupId>
        <artifactId>teavm-maven-plugin</artifactId>
        <version>${version.teavm}</version> <!-- propriétée hérité du common -->
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
</dependencies
```

* Configurer votre page html comme ceci

```
<head>
    
    ...
    
    <script type="text/javascript" charset="utf-8" src="/common-teavm/classes.js"></script>
    <script  type="text/javascript">main();</script>

    ...

</head>

```

* D'écrire vos classes Java en utilisant les annotation JsClass et JsMethod, comme ceci

```
import com.calinfo.api.common.teavm.annotations.JsClass;
import com.calinfo.api.common.teavm.annotations.JsMethod;

@JsClass(namespace = "MonNameSpace")
public class MyJsClass {

    @JsMethod(name = "maMethod")
    static int calculateSize(String[] tab){
        return tab.length;
    }

}
```

Le code java ci-dessus aura pour effet d'écrire la fonction javascript accessible depuis **document.$server.MonNameSapce.maMethod(...)**.

* Attention en intégrant common-teaVm, cela vous génère une nouvelle classe avec un *main*. Le démarrage de votre application
springboot risque de ne plus fonctionner. Vous dever indiquer dans votre configuration quel est la vrai classe mai nà utiliser
par spring boot. Vous devriez avoir quelque chose comme ceci :

```
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>${version.spring}</version>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <mainClass>com.calinfo.api.Application</mainClass>
    </configuration>
</plugin>
```

# Contrainte Java

La bibliothèque TeaVM apporte cependant queleques contraintes sur la classe Java déportable

* Le constructeur de la classe ne doit comporté aucun paramètre
* La classe ne peut pas utiliser l'injection
* Les méthodes annotées avec JsMethod ne peuvent pas renvoyer d'exception. Cependant le RuntimeException (ou dérivé) est géré.
* les types gérés en paramètre ou en retour sont *void*, *int* (le *long* n'est pa géré), *double*, *float*, *short*, *byte*,
*boolean*, *java.lang.String*, ainsi que les tableaux ([] et non des listes) de tous ces types.

# Configuration avancée

* Vous pouvez changer le nom du package et de la class Main de TeaVM en compilant votre programme avec les propriétés systèmes
*calinfo.common.teavm.main.packageName* et *calinfo.common.teavm.main.className"

Exemple : 
    
    mvn clean install -Dcalinfo.common.teavm.main.packageName=pkg,calinfo.common.teavm.main.className=MaClass
    
 La commande ci-dessus généra la classe *pkg.MaClass* et de ce fait dans votre pom.xml, il faudra référencer ceci :
 
    <mainClass>pkg.MaClass</mainClass>
 
 au lieu de :
 
    <mainClass>com.calinfo.teavm.JsClass</mainClass>

* Vous pouvez modifier la racine du namespace en modifiant la propriété system *calinfo.common.teavm.main.rootNameSpace*

Exemple :

    mvn clean install -Dcalinfo.common.teavm.main.rootNameSpace=document.$root

Avec la commande ci-dessus, vous pourrez accéder aux fonction javascript à partir de **document.$root** au lie de **document.$server**

* Vous pouvez avoir plus de log sur la compilation en mettant *trace* dans la propriété système *calinfo.common.teavm.log.level*
