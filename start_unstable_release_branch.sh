#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters, this script takes one parameter, the version of the release"
fi
VERSION=$1
git checkout develop
git hf release start $VERSION
mvn versions:set -DnewVersion=$VERSION
find . -name "pom.xml" -type f -exec  sed -i "s/<seqware-version>$VERSION-SNAPSHOT<\/seqware-version>/<seqware-version>$VERSION<\/seqware-version>/g" {} \;
git add pom.xml \*/pom.xml
git commit -m "Iterate version numbers to $VERSION"
git push

echo "Please perform any required test and last minute changes before a release"
