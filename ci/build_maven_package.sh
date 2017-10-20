#!/bin/bash

REPO_DIR=$(readlink -f  ../)

pushd $REPO_DIR
mvn clean package
mvn package

echo "jar file available at "$REPO_DIR"/target"
popd