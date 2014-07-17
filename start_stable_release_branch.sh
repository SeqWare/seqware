#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters, this script takes two parameters, the version of the current, and the version of the release"
fi
VERSION=$2
OLD_VERSION=$1
git checkout master
git hf release start $VERSION
mvn versions:set -DnewVersion=$VERSION
find . -name "pom.xml" -type f -exec  sed -i "s/<seqware-version>$OLD_VERSION<\/seqware-version>/<seqware-version>$VERSION<\/seqware-version>/g" {} \;
git add pom.xml \*/pom.xml
git commit -m "Iterate version numbers from $OLD_VERSION to $VERSION"
git push

echo "Please perform any required test and last minute changes before a release"
