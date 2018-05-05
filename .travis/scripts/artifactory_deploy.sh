#!/bin/bash

# Paramètre du script
groupId=com.calinfo-nc.services
artifactId=common
version=1.0-SNAPSHOT
packaging=jar
pathPomFile=$TRAVIS_BUILD_DIR/common
buildFileName=common-1.0-SNAPSHOT.jar

# Variable du programme
baseUrl=https://calinfo.artifactoryonline.com/calinfo

# Reconstitution de chemin de l'url dans Artifactory
pathGroupId=$(echo $groupId | sed -e "s/\./\//g")
pathArtifactId=$(echo $artifactId | sed -e "s/\./\//g")
path=$pathGroupId/$pathArtifactId/$version

# Reconstruction du nom de l'artifact dans Artifactory
artifactName=$artifactId-$version

# Reconstruction du repository dans Artifactory
repository=ext-release-local
if [[ ${version,,} = *-snapshot ]]
then
    repository=ext-snapshot-local
fi

curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "$baseUrl/$repository/$path/$artifactName.$packaging" -T "$pathPomFile/target/$buildFileName"

if [ "$packaging" != "pom" ]
then
    curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "$baseUrl/$repository/$path/$artifactName.pom" -T "$pathPomFile/pom.xml"
fi
