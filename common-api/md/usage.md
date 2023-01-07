
# Les services

Sur un *CRUD*, les opérations au niveau des services sont toujours les mêmes.  *Create*, *Read*, *Update* et *Delete*.

Suivant les projets, les termes peuvent varier. Par exemple, pour un *Read*, tantôt le développeur peut utiliser le verbe *read*, tantôt le développeur peut utiliser le verbe *load* (ou tout autre verbe...)

Afin d'harmoniser les verbes utilisés, le *common* apporte les interfaces suivantes dans le package *com.calinfo.api.common.service*

* ***C**reateService* : Permet de créer une ressource
* ***R**eadService* ou **R**eadCurrentService : Permet d'accéder en lecture à une ressource
* ***U**pdateService*: Permet de mettre à jour une ressource
* ***D**estroyService* ou ***D**eleteService* : Permet de supprimer une ressource

Il existe aussi deux autres interfaces permettant de lister les ressources. Ces interfaces sont :
* *ListService* : Permet de lister les ressources grâce à une *query*
* *LookupService* : Permet aussi de lister les ressources grâce à du *fullText*

# Les DTOs

Un DTO (Data Transfert Object), est un objet sérialisable utilisé comme paramètre aux services, ou encore comme attribut d'un autre DTO. Un DTO implémente l'interface *com.calinfo.api.common.dto.Dto*.

Le package *com.calinfo.api.common.dto* offre un ensemble de DTO utilitaires. Parmi eux, vous trouverez, entre autres :

* *MediaDto* : Représente un média (ex : Une image, une vidéo, un document, etc...)
* *PageInfoDto* : Ce DTO représente les paramètres de chargement dynamique d'une liste. Par exemple, il est fortement déconseillé dans un service de renvoyer une liste sur laquelle le développeur n'a pas de maîtrise sur la volumétrie des informations renvoyées. Si le développeur veut garder cette maîtrise, il devra, a minima, ajouter dans les paramètres de son service, le *start* et le *limit*.
* *DynamicListDto* : Dans le même esprit que *PageInfoDto*, si l'appelant du service renseigne les éléments à charger (*start* et *limit*), ce même appelant doit avoir en retour la liste demandée ainsi que le nombre total d'éléments que pourrait avoir cette liste si elle était renvoyée dans sa totalité.


# L'API de validation

L'API *javax.validation* offre la possibilité d'utiliser les *groups* dans chacune des annotations proposées par cette même API.

Le package *com.calinfo.api.common.validation* offre des interfaces, telles que *Create*, ou encore *Update*, pouvant être utilisées par les attributs *groups* des annotations de validation.
