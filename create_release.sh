#!/usr/bin/env bash

if [ "$#" -ne 2 ]
then
    echo "Please specify the release version number and the next snapshot for this release:"
    echo " ./create_release.sh 1.8 1.9-SNAPSHOT"
    exit
fi

# Exit when a command fails.
set -e

echo $1 > src/main/java/com/rolfje/anonimatron/version.txt
mvn versions:set -DnewVersion=$1
mvn clean assembly:assembly
mvn versions:commit

git add pom.xml src/main/java/com/rolfje/anonimatron/version.txt
git commit -m "Release $1"
git tag "v$1"

echo $2 > src/main/java/com/rolfje/anonimatron/version.txt
mvn versions:set -DnewVersion=$2
mvn versions:commit

git add pom.xml src/main/java/com/rolfje/anonimatron/version.txt
git commit -m "Update version to $2"

echo "The file to upload for this release is:"
echo
ls -l target/*bin.zip
echo
echo "When creating a release, github automatically adds src zip"
echo "files, you only need to upload the binary. Remove the bin"
echo "in the filename for a more user-friendly release name."
echo
echo "Don't forget to push incuding tags."