#!/usr/bin/env bash
set -e
cd -P "$(dirname $0)"
java ${JAVA_OPTS:='-Xmx2G'} \
  -classpath *:./libraries/*:./jdbcdrivers/*:./anonymizers/* \
  --add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED \
  com.rolfje.anonimatron.Anonimatron $*
cd -