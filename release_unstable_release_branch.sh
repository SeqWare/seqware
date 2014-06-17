#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

function jsonval {
    temp=`echo $json | sed 's/\\\\\//\//g' | sed 's/[{}]//g' | awk -v k="text" '{n=split($0,a,","); for (i=1; i<=n; i++) print a[i]}' | sed 's/\"\:\"/\|/g' | sed 's/[\,]/ /g' | sed 's/\"//g' | grep -w $prop`
    echo ${temp##*|}
}

if [ "$#" -ne 3 ]; then
    echo "Illegal number of parameters, this script takes three parameters, version of the release, version of the next release on develop, and an OAuth token for GitHub"
fi
VERSION=$1
NEXT_VERSION=$2
TOKEN=$3

#git tag $VERSION
#git push --tags origin
#mvn release:perform -DconnectionUrl=scm:git:git@github.com:SeqWare/seqware.git -Dtag=$VERSION

# upload non-jar artifacts to github

# create release
#echo -e "{\"tag_name\":\""$VERSION"\", \"name\":\""$VERSION"\", \"body\":\""Automated release message"\"}" > release.json
#curl -u $TOKEN:x-oauth-basic -X POST -d @release.json https://api.github.com/repos/SeqWare/seqware/releases?tag_name=$VERSION&name=$VERSION
# extract release number, relies upon order of releases which can be improved
#GITHUB_RELEASE_NUMBER=`curl -i https://api.github.com/repos/SeqWare/seqware/releases | grep \"id\" | head -n1 | cut -d':' -f2 | cut -d',' -f1 | tr -d ' '`
#curl -u $TOKEN:x-oauth-basic -X POST --data-binary @seqware-pipeline/target/seqware https://uploads.github.com/repos/SeqWare/seqware/releases/$GITHUB_RELEASE_NUMBER/assets?name=seqware --header "Content-Type:text/plain" 
#curl -u $TOKEN:x-oauth-basic -X POST --data-binary @seqware-pipeline/target/archetype-catalog.xml https://uploads.github.com/repos/SeqWare/seqware/releases/$GITHUB_RELEASE_NUMBER/assets?name=archetype-catalog.xml --header "Content-Type:text/xml" 

# if the following dies with a merge error, run git mergetool -t kdiff3
git checkout develop
git merge release/$VERSION
mvn versions:set -DnewVersion=$NEXT_VERSION-SNAPSHOT
find . -name "pom.xml" -type f -exec  sed -i "s/<seqware-version>$VERSION-SNAPSHOT<\/seqware-version>/<seqware-version>$NEXT_VERSION-SNAPSHOT<\/seqware-version>/g" {} \;
git add pom.xml \*/pom.xml
git commit -m "Iterate version numbers to $NEXT_VERSION"
git push
# git push origin --delete release/$VERSION
