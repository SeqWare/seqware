#!/bin/bash -vx

su - seqware -c 'cd /home/seqware/workflow-dev; \
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
-DworkflowName=HelloWorld'

su - seqware -c 'cd /home/seqware/workflow-dev/HelloWorld; mvn install'

su - seqware -c 'cd /home/seqware/workflow-dev/HelloWorld; seqware bundle package --dir target/Workflow_Bundle_HelloWorld*'

su - seqware -c 'cd /home/seqware/workflow-dev/HelloWorld; seqware bundle install --zip Workflow_Bundle_HelloWorld*'

