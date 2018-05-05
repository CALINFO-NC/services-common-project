#!/bin/bash

# Paramètre du script
groupId=com.calinfo-nc.services
artifactId=common
version=1.0-SNAPSHOT
packaging=jar
pathPomFile=$TRAVIS_BUILD_DIR/common

# Variable du programme
baseUrl=https://calinfo.artifactoryonline.com/calinfo

# Reconstitution de chemin de l'url dans Artifactory
pathGroupId=$(echo $groupId | sed -e "s/\./\//g")
pathArtifactId=$(echo $artifactId | sed -e "s/\./\//g")
path=$pathGroupId/$pathArtifactId/$version

# Reconstruction du nom de l'artifact dans Artifactory
artifactName=$artifactId-$version

echo "$baseUrl/ext-snapshot-local/$path/$artifactName.$packaging"

if ["$packaging" != "pom" ]
then
    echo "publier pom"
fi

#curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "$baseUrl/ext-snapshot-local/$path/$artifactName.$packaging" -T "$pathPomFile/target/common-1.0-SNAPSHOT.jar"
#curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "$baseUrl/ext-snapshot-local/$path/$artifactName.pom" -T "$pathPomFile/pom.xml"