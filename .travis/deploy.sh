#!/bin/bash

commonPathPomFile=$1
commonBbuildFileName=$2

export SCRIPT_BASE_DIR=.travis-github/scripts

source $SCRIPT_BASE_DIR/script_init.sh

artifactory_deploy $commonPathPomFile $commonBbuildFileName