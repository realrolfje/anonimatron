@echo off
java -Xmx2G --add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED -classpath *;./libraries/*;./jdbcdrivers/*;./anonymizers/* com.rolfje.anonimatron.Anonimatron %*
