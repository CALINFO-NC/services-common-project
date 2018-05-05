#!/bin/bash

# Paramètre du script
groupId=$1
artifactId=$2
version=$3
packaging=$4
pathPomFile=$5
buildFileName=$6

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