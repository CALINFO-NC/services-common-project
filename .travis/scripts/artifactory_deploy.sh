#!/bin/bash

echo $USER
curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT \"https://calinfo.artifactoryonline.com/calinfo/ext-snapshot-local/com/calinfo-nc/services/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.jar\" -T \"$HOME/common/target/common-1.0-SNAPSHOT.jar"
curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT \"https://calinfo.artifactoryonline.com/calinfo/ext-snapshot-local/com/calinfo-nc/services/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.pom\" -T \"$HOME/common/pom.xml"