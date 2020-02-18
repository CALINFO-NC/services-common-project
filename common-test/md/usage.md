# Créé des tests Java compatible Xray

 Ce module apporte des outils permettant d'intgrer les résultats de test dans Jira via le module XRay.
 Pour ce faire, l'ensemble des tests doivent être écrit avec TestNg.
 Ci dessous, un exemple de test.

```
public class WorkflowWarehouseTransfertTest extends AbstractTestNGSpringContextTests {

    @Test
    @Xray(requirement = "CODEJIRA-1")
    private void envoyerTransfertEntrepot() throws Exception {
        ...
    }

}
```

 Lorsque ce test s'exécutera, le résultat de ce test créra une nouvelle Jira (*CODEJIRA-100* apr exemple) qui sera associé à la jira *CODEJIRA-1*.

 Afin d'éviter qu'à chaque exécution de ce test, une nouvelle Jira soit créé, il faudra indiquer dans le code de test, le numéro de la Jira créé (*CODEJIRA-100* dans l'exemple ci-dessus).
 Pour cela il fadra utiliser la propriété *test* de l'annotation *Xray* comme ci-dessous.


```
public class WorkflowWarehouseTransfertTest extends AbstractTestNGSpringContextTests {

    @Test
    @Xray(requirement = "CODEJIRA-1", test = "CODEJIRA-100")
    private void envoyerTransfertEntrepot() throws Exception {
        ...
    }

}
```

 L'exécution des tests généra un fichier testng-results.xml. Ce fichier devra être envoyé au serveur Xray pour obtenir le résultat dans Jira

 Il est possible via l'annotation *Xray* de définir un ratachement d'un test à plusieur Jira. La propriété *requirement* est un tableau.
 Cependant, XRay étant bugué pour le moment, seule le premier éléments du tableau est rataché automatiquement. Les autres éléments devront faire
 l'objet d'un ratachement manuel.

# Comment envoyer les résultats des tests Java dans le serveur Xray

 Ce référer à la documentation officiel : https://confluence.xpand-it.com/

 Cependant, vous trouverez ci-dessous un exemple de script sh intégrant le résultat des tests dans le serveur Xray

```
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