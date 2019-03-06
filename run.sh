#!/bin/sh

baseDir=$(dirname `readlink -f "$0"`)

$baseDir/build.sh

docker rm -f cleanpath-ui
docker rm -f cleanpath-service
docker rm -f cleanpath-cleaner
docker network rm cleanPath

docker network create cleanPath
docker run -d --name cleanpath-cleaner --network cleanPath cleanpath-cleaner:1.0.0
docker run -d --name cleanpath-service --network cleanPath cleanpath-service:1.0.0
docker run -d --name cleanpath-ui --network cleanPath -p 1337:80 cleanpath-ui:1.0.0