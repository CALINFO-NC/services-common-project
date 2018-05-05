#!/bin/bash

groupId=com.calinfo-nc.services
artifactId=common
version=1.0-SNAPSHOT
packaging=jar

pathGroupId=$(echo $groupId | sed -e "s/\./\//g")
pathArtifactId=$(echo $artifactId | sed -e "s/\./\//g")
path=$pathGroupId/$pathArtifactId/version
echo $path

#curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "https://calinfo.artifactoryonline.com/calinfo/ext-snapshot-local/com/calinfo-nc/services/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.jar" -T "$TRAVIS_BUILD_DIR/common/target/common-1.0-SNAPSHOT.jar"
#curl -u $ARTIFACTORY_USER:$ARTIFACTORY_TOKEN -X PUT "https://calinfo.artifactoryonline.com/calinfo/ext-snapshot-local/com/calinfo-nc/services/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.pom" -T "$TRAVIS_BUILD_DIR/common/pom.xml"