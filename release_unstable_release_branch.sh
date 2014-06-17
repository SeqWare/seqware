#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters, this script takes three parameters, version of the release, version of the next release on develop, and an OAuth token for GitHub"
fi
VERSION=$1
NEXT_VERSION=$2
TOKEN=$3

git tag $VERSION
git push --tags origin
mvn release:perform -DconnectionUrl=scm:git:git@github.com:SeqWare/seqware.git -Dtag=$VERSION

# upload non-jar artifacts to github
curl -u $TOKEN:x-oauth-basic -X POST -d @seqware-pipeline/target/seqware https://uploads.github.com/repos/SeqWare/seqware/releases/$VERSION/assets?name=seqware --header "Content-Type:text/plain"
curl -u $TOKEN:x-oauth-basic -X POST -d @seqware-pipeline/target/archetype-catalog.xml https://uploads.github.com/repos/SeqWare/seqware/releases/$VERSION/assets?name=archetype-catalog.xml --header "Content-Type:text/plain"

# if the following dies with a merge error, run git mergetool -t kdiff3
git checkout develop
git merge release/$VERSION
mvn versions:set -DnewVersion=$NEXT_VERSION-SNAPSHOT
find . -name "pom.xml" -type f -exec  sed -i "s/<seqware-version>$VERSION-SNAPSHOT<\/seqware-version>/<seqware-version>$NEXT_VERSION-SNAPSHOT<\/seqware-version>/g" {} \;
git add pom.xml \*/pom.xml
git commit -m "Iterate version numbers to $NEXT_VERSION"
git push
# git push origin --delete release/$VERSION
