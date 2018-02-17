#!/bin/bash

REPO_DIR=$1
if [ -z ${REPO_DIR} ]; then
    echo "Base dir needed"
    exit 1
fi

echo "Base dir "$REPO_DIR

pushd $REPO_DIR
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
mvn clean package
mvn package

if [ $? -ne 0 ]; then
    echo "Failed to build package"
    exit 1
fi

echo "jar file available at "$REPO_DIR"/target"
popd