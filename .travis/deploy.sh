#!/bin/bash

# Import des scripts travis
source $SCRIPT_BASE_DIR/scripts/artifactory.sh

# Effectuer les déploiements sur artifactory
artifactory_deploy $TRAVIS_BUILD_DIR/common common-1.0-SNAPSHOT.jar
artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-1.0-SNAPSHOT.jar
artifactory_deploy $TRAVIS_BUILD_DIR/common-libs pom.xml