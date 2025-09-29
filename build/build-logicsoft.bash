#!/bin/bash

set -xe

SCRIPT=$(realpath "$0")
SCRIPT_PATH=$(dirname "$SCRIPT")
APP="pos-connector"

cd "$SCRIPT_PATH"/.. || exit

cp src/main/resources/application_logicsoft.yml src/main/resources/application.yml

docker build --build-arg deployment=logicsoft -f build/Dockerfile -t "$APP" .

docker stop "$APP" || true
docker rm "$APP" || true

docker run --name "$APP" -i -d --restart always -p 8080:8080 "$APP":latest
