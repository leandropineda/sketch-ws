#!/usr/bin/env bash
REPO_DIR=$1
if [ -z ${REPO_DIR} ]; then
    echo "Base dir needed"
    exit 1
fi

echo "Base dir "$REPO_DIR

JAR_FILE="sketchws-1.0-SNAPSHOT.jar"

echo "================================================"
echo "=============== Building package ==============="
echo "================================================"

./build_maven_package.sh $REPO_DIR

if [ $? -ne 0 ]; then
    echo "Failed to build package"
    exit 1
fi

echo "==================================================="
echo "================== Copying files =================="
echo "==================================================="

cp $REPO_DIR/target/$JAR_FILE .
cp $REPO_DIR/config.yml .

