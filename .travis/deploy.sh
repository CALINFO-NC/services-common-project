#!/bin/bash

# Import des scripts travis
source $SCRIPT_BASE_DIR/scripts/artifactory.sh


# Affichage des variable travis
echo TRAVIS_TAG=$TRAVIS_TAG
echo TRAVIS_BRANCH=$TRAVIS_BRANCH
echo TRAVIS_PULL_REQUEST_BRANCH=$TRAVIS_PULL_REQUEST_BRANCH

# Si on est sur une branche snapshot
isMasterBranch="false"
if [ "$TRAVIS_BRANCH" == "master" ] && [ "$TRAVIS_TAG" == "" ] && [ "$TRAVIS_PULL_REQUEST_BRANCH" == "" ]
then
   isMasterBranch="true"
fi
echo isMasterBranch=$isMasterBranch

# Publier l'image docker
version=$(maven_version "$TRAVIS_BUILD_DIR/pom.xml")
echo version=$version
isSnapshot=$(maven_is_snapshot "$version")
echo isSnapshot=$isSnapshot

if [ "$isSnapshot" == "false" ]
then
    export ARTIFACTORY_RELEASE_REPOSITORY=license-gpl-local
fi


# On déploie le binaire dans artifactory uniquement si c'est une vrai version ou si c'est dans le master
if [ "$isSnapshot" == "false" ] || [ "$isMasterBranch" == "true" ]
then
    
    artifactory_deploy $TRAVIS_BUILD_DIR/common common.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common common-sources.jar "" sources

    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-sources.jar "" sources

    artifactory_deploy $TRAVIS_BUILD_DIR/common-test common-test.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test common-test-sources.jar "" sources

    artifactory_deploy $TRAVIS_BUILD_DIR/common-io common-io.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-io common-io-sources.jar "" sources

    artifactory_deploy $TRAVIS_BUILD_DIR/common-teavm common-teavm.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-teavm common-teavm-sources.jar "" sources

    artifactory_deploy $TRAVIS_BUILD_DIR pom.xml
fi
