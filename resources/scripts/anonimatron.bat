@echo off
java -Xmx2G -classpath *;./libraries/*;./jdbcdrivers/*;./anonymizers/*   --add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED com.rolfje.anonimatron.Anonimatron %*
