#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
docker-compose -f docker-compose.dev.yml up -d --build --force-recreate
