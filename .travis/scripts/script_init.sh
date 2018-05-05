#!/bin/bash

# Paramètre du script
baseScriptPath=$1


chmod +x $baseScriptPath/scripts/artifactory_deploy.sh
chmod +x $baseScriptPath/scripts/test.sh

export $SCRIPT_BASE_DIR=$baseScriptPath

$SCRIPT_BASE_DIR/scripts/test.sh