#!/usr/bin/env bash
REPO_DIR=../

JAR_FILE="sketchws-1.0-SNAPSHOT.jar"

echo "================================================"
echo "=============== Building package ==============="
echo "================================================"

./build_maven_package.sh

echo "==================================================="
echo "=============== Building Dockerfile ==============="
echo "==================================================="

WORK_DIR="event_receiver_tmp"

rm -rf ./$WORK_DIR
mkdir ./$WORK_DIR

cp $REPO_DIR/target/$JAR_FILE ./$WORK_DIR
cp $REPO_DIR/config.yml ./$WORK_DIR
cp compose/Dockerfile ./$WORK_DIR
cp compose/docker-compose.yml ./$WORK_DIR

pushd ./$WORK_DIR
echo "If the image doesn't exist an error will be shown. You can ignore it since it's not critical."

docker rmi event_receiver
docker build . -t event_receiver:latest

popd
rm -rf ./$WORK_DIR
