#!/bin/bash

# Import des scripts travis
source $SCRIPT_BASE_DIR/scripts/artifactory.sh

# Effectuer les déploiements sur artifactory
if [ "$TRAVIS_BRANCH" == "master" ]
then
    artifactory_deploy $TRAVIS_BUILD_DIR/common common-1.0-SNAPSHOT.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-1.0-SNAPSHOT.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-libs pom.xml
fi

# Publier le site si nécessaire
isSnapshot=$(maven_is_snapshot "$TRAVIS_BUILD_DIR/pom.xml")
if [ "$isSnapshot" == "false" ] || [ "$TRAVIS_BRANCH" == "master" ]
then
    mvn site -B
fi