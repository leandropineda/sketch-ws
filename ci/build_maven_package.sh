#!/bin/bash

# run with JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 option
REPO_DIR=../

pushd $REPO_DIR
mvn clean package
mvn package

echo "jar file available at "$REPO_DIR"/target"
popd