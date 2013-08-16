#!/bin/bash -vx

# Should be invoked as seqware user
# e.g.: sudo -i -u seqware hello-world-workflow.sh

cd ~/workflow-dev

mvn archetype:generate \
-DinteractiveMode=false \
-DarchetypeCatalog=local \
-DarchetypeGroupId=com.github.seqware \
-DarchetypeArtifactId=seqware-archetype-java-workflow \
-DgroupId=io.seqware \
-Dpackage=io.seqware \
-DartifactId=HelloWorld \
-Dversion=1.0-SNAPSHOT \
-DworkflowVersion=1.0-SNAPSHOT \
-DworkflowDirectoryName=HelloWorld \
-DworkflowName=HelloWorld

cd HelloWorld

mvn install

seqware bundle package --dir `ls -d target/Workflow_Bundle_HelloWorld*`

seqware bundle install --zip `ls Workflow_Bundle_HelloWorld*`
