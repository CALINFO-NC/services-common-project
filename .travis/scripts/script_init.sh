#!/bin/bash

# Paramètre du script
baseScriptPath=$1


chmod +x $baseScriptPath/artifactory_deploy.sh
chmod +x $baseScriptPath/test.sh

export SCRIPT_BASE_DIR=$baseScriptPath

$SCRIPT_BASE_DIR/test.sh