#!/bin/bash

# Import des scripts travis
source $SCRIPT_BASE_DIR/scripts/artifactory.sh

# Effectuer les déploiements sur artifactory
if [ "$TRAVIS_BRANCH" == "master" ]
then
    artifactory_deploy $TRAVIS_BUILD_DIR/common common.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common common-javadoc-resources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common common-sources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-javadoc-resources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-sources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-test.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-test-javadoc-resources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-test-sources.jar
    artifactory_deploy $TRAVIS_BUILD_DIR/common-libs pom.xml
fi

#  La publicaion sur le maven sit est trop long, et plante une foie sur trois. Voir comment faire autrement
# Publier le site si nécessaire
isSnapshot=$(maven_is_snapshot "$TRAVIS_BUILD_DIR/pom.xml")
# if [ "$TRAVIS_BRANCH" == "master" ] && [ "$isSnapshot" == "false" ]
# then
    mvn site -B
# fi