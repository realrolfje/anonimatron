#!/usr/bin/env bash
set -e
cd -P "$(dirname $0)"
java ${JAVA_OPTS:='-Xmx=2G'} -classpath *:./libraries/*:./jdbcdrivers/*:./anonymizers/* com.rolfje.anonimatron.Anonimatron $*
cd -