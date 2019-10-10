#!/bin/bash

# Import des scripts travis
source $SCRIPT_BASE_DIR/scripts/artifactory.sh

# Effectuer les déploiements sur artifactory
if [ "$TRAVIS_BRANCH" == "master" ]
then
    artifactory_deploy $TRAVIS_BUILD_DIR/common common.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-javadoc-resources common-javadoc-resources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-sources common-sources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api-javadoc-resources common-api-javadoc-resources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api-sources common-api-sources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test common-test.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test-javadoc-resources common-test-javadoc-resources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-test-sources common-test-sources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-libs pom.xml
fi

# Publier le site si nécessaire
# isSnapshot=$(maven_is_snapshot "$TRAVIS_BUILD_DIR/pom.xml")
# if [ "$TRAVIS_BRANCH" == "master" ] && [ "$isSnapshot" == "false" ]
if [ "$TRAVIS_BRANCH" == "master" ]
then
    mvn site -B
fi