#!/usr/bin/env bash
cd $(realpath $(dirname $0))
java -Xmx2G -classpath *:./libraries/*:./jdbcdrivers/*:./anonymizers/* com.rolfje.anonimatron.Anonimatron $*
cd -