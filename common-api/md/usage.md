# Les services

 Sur un *CRUD*, les opérations au niveau des services sont toujours les mêmes.
 *Create*, *Read*, *Update* et *Delete*.

 Suivant les projets, les termes peuvent varier. Par exemple, pour un *Read*, tantôt le développeur peut utiliser le verbe
 *read*, tantôt le développeur peut utiliser le verbe *load* (ou tout autre verbe...)

 Afin d'harmoniser les verbes utilisés, le *common* apporte les interfaces suivantes dans le package *com.calinfo.api.common.service*

 * *CreateService* ou *CreateProjectionService*

 * *ReadService*

 * *UpdateService* ou *UpdateProjectionService*

 * *DestroyService* ou *DeleteService*

# Les DTOs

 Un DTO (Data Transfert Object), est un objet sérialisable utilisé comme paramètre aux services, ou encore comme attribut d'un autre DTO.
 Un DTO implémente l'interface *com.calinfo.api.common.dto.Dto*.

 Le package *com.calinfo.api.common.dto* offre un ensemble de DTO utilitaires. Parmis eux, vous trouverez, entres autres :

 * *MediaDto* : Représente un média (ex : Une image, une vidéo, un document, etc...)

 * *ChargementInfoDto* : Ce DTO représente les paramètres de chargement dynamique d'une liste.
 Par exemple, il est fortement déconseillé dans un service de renvoyer une liste sur laquelle le développeur n'a pas de
 maîtrise sur la volumétrie des informations renvoyées. Si le développeur veut garder cette maîtrise, il devra, à minima, ajouter
 dans les paramètres de son service, le *départ* et la *limite*.

 * *DynamicListDto* : Dans le même esprit que *ChargementListDto*, si l'appelant du service renseigne les éléments à charger (*départ* et *limite*),
 ce même appelant doit avoir en retour la liste demandée ainsi que le nombre total d'éléments que pourraient avoir cette liste.

# Les Ressources

 Une ressource est la spécialisation d'un DTO. De ce fait, une ressource hérite du DTO.
 La ressource représente une donnée exposée par un service (par exemple un *CRUD*).

 Pour représenter une ressource dans vos applications, il est important que chaque classe représentant la ressource
 hérite de *com.calinfo.api.common.resource.Resource*.

 Dans le package *com.calinfo.api.common.resource*, vous trouverez d'autres types de ressources vous aidant à harmoniser
 les services REST dans l'ensemble de vos projets.

# L'API de validation

 L'API *javax.validation* offre la possibilité d'utiliser les *groups* dans chacune des annotations proposées par cette même API.

 Le package *com.calinfo.api.common.validation* offre des interfaces, telles que *Create*, ou encore *Update*,
 pouvant être utilisées par les attributs *groups* des annotations de validation.