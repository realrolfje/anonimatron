#!/usr/bin/env bash
set -e
cd -P "$(dirname "$0")"

JAVA_OPTS=${JAVA_OPTS:-'-Xmx2G'}
ADD_OPENS='--add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED'

if java -version 2>&1 | head -n 1 | grep -q 'version "1\.8'; then
  ADD_OPENS=''
fi

java ${JAVA_OPTS} ${ADD_OPENS} -classpath '*:./libraries/*:./jdbcdrivers/*:./anonymizers/*' com.rolfje.anonimatron.Anonimatron "$@"
