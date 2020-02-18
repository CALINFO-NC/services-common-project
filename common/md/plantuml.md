# Description

 Le common intègre la génération d'image UML grace à l'outil PlantUML dont vous pouvez trouver la description ici : http://plantuml.com/fr/}http://plantuml.com/fr/

# Usage

 Afin d'utiliser plant UML, il suffit de créer le répertoire src/site/plantuml et d'y ajouter les fichiers "*.txt" contenant les scripts PlantUML.
 Ce comportement peut être changé en modifiant les propriétés maven *plantuml.source-file-including* et *plantuml.source-directory*.

 Les images issus de ces fichiers seront automatiquement générées dans le répertoire *target/site/images/plantuml*.
 Ce comportement peut être changé en modifiant la propriété maven *plantuml.output-directory*.

# Point d'attention

 Le common est configuré afin qu'à chaque exécution de la commande maven *site*, les images de PlantUML soit générées automatiquement.
