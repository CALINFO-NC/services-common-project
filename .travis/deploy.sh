#!/bin/bash

# Import des scripts travis
export SCRIPT_BASE_DIR=$TRAVIS_BUILD_DIR/.travis-github/scripts
source $SCRIPT_BASE_DIR/artifactory.sh

# Effectuer les déploiements sur artifactory
artifactory_deploy $TRAVIS_BUILD_DIR/common common-1.0-SNAPSHOT.jar
artifactory_deploy $TRAVIS_BUILD_DIR/common-api common-api-1.0-SNAPSHOT.jar
artifactory_deploy $TRAVIS_BUILD_DIR/common-libs pom.xml