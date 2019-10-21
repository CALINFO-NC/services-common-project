#!/bin/bash

# Import des scripts travis
source $SCRIPT_BASE_DIR/scripts/artifactory.sh


version=$(maven_version "$TRAVIS_BUILD_DIR/pom.xml")
isSnapshot=$(maven_is_snapshot "$version")
if [ "$isSnapshot" == "false" ]
then
    export ARTIFACTORY_RELEASE_REPOSITORY=license-gpl-local
fi

# Effectuer les déploiements sur artifactory
if [ "$TRAVIS_BRANCH" == "master" ]
then
    artifactory_deploy $TRAVIS_BUILD_DIR/common common.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common common-sources.jar "" sources
    artifactory_deploy $TRAVIS_BUILD_DIR/common common-javadoc.jar "" javadoc
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-sources.jar "" sources
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-javadoc.jar "" javadoc
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test common-test.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test common-test-sources.jar "" sources
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test common-test-javadoc.jar "" javadoc
    artifactory_deploy $TRAVIS_BUILD_DIR/common-libs pom.xml
fi


# Publier le site si nécessaire
if [[ ( "$TRAVIS_BRANCH" == "master" && "$isSnapshot" == "false" ) || ( $TRAVIS_PULL_REQUEST != "false" ) ]]
then
    mvn site -B
fi