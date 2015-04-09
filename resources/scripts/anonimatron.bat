@echo off
java -Xmx2G -classpath *;./libraries/*;./jdbcdrivers/*;./anonymizers/* com.rolfje.anonimatron.Anonimatron %*
