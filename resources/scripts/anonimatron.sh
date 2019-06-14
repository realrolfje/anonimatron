#!/usr/bin/env bash
cd -P "$(dirname $0)"
java -Xmx2G -classpath *:./libraries/*:./jdbcdrivers/*:./anonymizers/* com.rolfje.anonimatron.Anonimatron $*
cd -