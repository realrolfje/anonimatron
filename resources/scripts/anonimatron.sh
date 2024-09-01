#!/usr/bin/env bash
set -e
cd -P "$(dirname $0)"
java ${JAVA_OPTS:='-Xmx2G'} --add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED -classpath *:./libraries/*:./jdbcdrivers/*:./anonymizers/* com.rolfje.anonimatron.Anonimatron $*
cd -