#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters, this script takes two parameters, version of the release and an OAuth token for GitHub"
fi
VERSION=$1
TOKEN=$2

git hf hotfix finish $VERSION
git push --tags origin
mvn release:perform -DconnectionUrl=scm:git:git@github.com:SeqWare/seqware.git -Dtag=$VERSION

# upload non-jar artifacts to github

# create release
echo -e "{\"tag_name\":\""$VERSION"\", \"name\":\""$VERSION"\", \"body\":\""Automated release message"\"}" > release.json
curl -u $TOKEN:x-oauth-basic -X POST -d @release.json https://api.github.com/repos/SeqWare/seqware/releases?tag_name=$VERSION&name=$VERSION
rm release.json
# extract release number, relies upon order of releases which can be improved
GITHUB_RELEASE_NUMBER=`curl -i https://api.github.com/repos/SeqWare/seqware/releases | grep \"id\" | head -n1 | cut -d':' -f2 | cut -d',' -f1 | tr -d ' '`
curl -u $TOKEN:x-oauth-basic -X POST --data-binary @seqware-pipeline/target/seqware https://uploads.github.com/repos/SeqWare/seqware/releases/$GITHUB_RELEASE_NUMBER/assets?name=seqware --header "Content-Type:text/plain" 
curl -u $TOKEN:x-oauth-basic -X POST --data-binary @seqware-pipeline/target/archetype-catalog.xml https://uploads.github.com/repos/SeqWare/seqware/releases/$GITHUB_RELEASE_NUMBER/assets?name=archetype-catalog.xml --header "Content-Type:text/xml" 
