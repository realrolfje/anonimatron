#!/usr/bin/env bash

if [ "$#" -ne 2 ]
then
    echo "Please specify the release version number and the next snapshot for this release:"
    echo " ./create_release.sh 1.8 1.9-SNAPSHOT"
    exit
fi

# Exit when a command fails.
set -e

# Set the new versions
echo $1 > src/main/java/com/rolfje/anonimatron/version.txt
mvn versions:set -DnewVersion=$1

# Deploy the release to mavenrepo
mvn clean deploy -P release

# Commit the release and tag it.
mvn versions:commit
git add pom.xml src/main/java/com/rolfje/anonimatron/version.txt
git commit -m "Release $1"
git tag "v$1"

# Set the version to the new SNAPSHOT version
echo $2 > src/main/java/com/rolfje/anonimatron/version.txt
mvn versions:set -DnewVersion=$2
mvn versions:commit

# Commit the SNAPSHOT version to git
git add pom.xml src/main/java/com/rolfje/anonimatron/version.txt
git commit -m "Update version to $2"

git push --follow-tags

# Sign the zip file
gpg -ab --default-key 45E2A5E085182DC26EFEF6E796BB2760490D54DD target/anonimatron*.zip

echo "The files to upload for this release are:"
echo
ls -l target/anonimatron*.zip*
echo
echo "When creating a release, github automatically adds src zip"
echo "files, you only need to upload the binary."
echo
