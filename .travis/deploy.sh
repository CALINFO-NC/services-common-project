#!/bin/bash

export SCRIPT_BASE_DIR=$TRAVIS_BUILD_DIR/.travis-github/scripts
source $SCRIPT_BASE_DIR/script_init.sh

artifactory_deploy $TRAVIS_BUILD_DIR/common common-1.0-SNAPSHOT.jar