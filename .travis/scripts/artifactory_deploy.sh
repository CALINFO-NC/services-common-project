#!/bin/bash

groupId=toto.fff.frfr\bbb\nnn
artifactId=$2
version=$3
packaging=$4

path=$(echo $groupId | sed -e 's/"."/"\"/')
echo $path

#curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "https://calinfo.artifactoryonline.com/calinfo/ext-snapshot-local/com/calinfo-nc/services/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.jar" -T "$TRAVIS_BUILD_DIR/common/target/common-1.0-SNAPSHOT.jar"
#curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "https://calinfo.artifactoryonline.com/calinfo/ext-snapshot-local/com/calinfo-nc/services/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.pom" -T "$TRAVIS_BUILD_DIR/common/pom.xml"