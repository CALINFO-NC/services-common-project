#!/bin/bash

# Indiquer le lieu de stockage des script Travis
export SCRIPT_BASE_DIR=$TRAVIS_BUILD_DIR/.travis-github/scripts

source $SCRIPT_BASE_DIR/artifactory.sh

# Effectuer un déploiement du common sur artifactory
artifactory_deploy $TRAVIS_BUILD_DIR/common common-1.0-SNAPSHOT.jar