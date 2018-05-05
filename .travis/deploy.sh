#!/bin/bash

commonPathPomFile=$1
commonBbuildFileName=$2

export SCRIPT_BASE_DIR=$TRAVIS_BUILD_DIR/.travis-github/scripts

# source $SCRIPT_BASE_DIR/script_init.sh

sudo apt-get install libxml2-utils

xml_xpath(){

	# Paramètre de la fonction
	xmlFile=$1
	xpath=$2

	tempfile=$(mktemp)
	echo tempfile=$tempfile
	sudo cat $xmlFile | sed '2 s/xmlns=".*"//g' > $tempfile
	echo cat OK
	var=$(echo "$2" | sudo xmllint $tempfile --xpath 'string('$xpath')')
	echo xmllint ok

	echo $var
}

maven_version(){

    echo maven_version $1
	# Paramètre de la fonction
	pomFile=$1	# Chemin complet du fichier pom.xml
	echo maven_version prm $pomFile

	val=$(xml_xpath $pomFile "/project/version")

	if [ "$val" == "" ]
	then
		val=$(xml_xpath $pomFile "/project/parent/version")
	fi

	echo $val
}

maven_groupId(){

	# Paramètre de la fonction
	pomFile=$1	# Chemin complet du fichier pom.xml

	val=$(xml_xpath $pomFile "/project/groupId")

	if [ "$val" == "" ]
	then
		val=$(xml_xpath $pomFile "/project/parent/groupId")
	fi

	echo $val
}

maven_artifactId(){

	# Paramètre de la fonction
	pomFile=$1	# Chemin complet du fichier pom.xml

	val=$(xml_xpath $pomFile "/project/artifactId")

	echo $val
}

maven_packaging(){

	# Paramètre de la fonction
	pomFile=$1	# Chemin complet du fichier pom.xml

	val=$(xml_xpath $pomFile "/project/packaging")

	echo $val
}

export ARTIFACTORY_RELEASE_REPOSITORY=ext-release-local
export ARTIFACTORY_SNAPSHOT_REPOSITORY=ext-snapshot-local

artifactory_deploy(){

	# Paramètre de la fonction
	pathPomFile=$1
	buildFileName=$2

	version=$(maven_version "$pathPomFile/pom.xml")
	groupId=$(maven_groupId "$pathPomFile/pom.xml")
	artifactId=$(maven_artifactId "$pathPomFile/pom.xml")
	packaging=$(maven_packaging "$pathPomFile/pom.xml")

	# Variable du programme
	baseUrl=https://calinfo.artifactoryonline.com/calinfo

	# Reconstitution de chemin de l'url dans Artifactory
	pathGroupId=$(echo $groupId | sed -e "s/\./\//g")
	pathArtifactId=$(echo $artifactId | sed -e "s/\./\//g")
	path=$pathGroupId/$pathArtifactId/$version

	# Reconstruction du nom de l'artifact dans Artifactory
	artifactName=$artifactId-$version

	# Reconstruction du repository dans Artifactory
	repository=$ARTIFACTORY_RELEASE_REPOSITORY
	if [[ ${version,,} = *-snapshot ]]
	then
    	repository=$ARTIFACTORY_SNAPSHOT_REPOSITORY
	fi

	curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "$baseUrl/$repository/$path/$artifactName.$packaging" -T "$pathPomFile/target/$buildFileName"

	if [ "$packaging" != "pom" ]
	then
    	curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "$baseUrl/$repository/$path/$artifactName.pom" -T "$pathPomFile/pom.xml"
	fi
}

artifactory_deploy $commonPathPomFile $commonBbuildFileName