#!/bin/sh

baseDir=$(dirname `readlink -f "$0"`)

cd $baseDir/cleaner

docker build --tag cleanpath-cleaner:1.0.0 -f ./docker/Dockerfile ./

cd $baseDir/service

docker build --tag cleanpath-service:1.0.0 -f ./docker/Dockerfile ./

cd $baseDir/ui

docker build --tag cleanpath-ui:1.0.0 -f ./docker/Dockerfile ./