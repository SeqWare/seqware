The first step to get started is to generate your workflow skeleton using Maven 
archetypes. You will want to do this in a directory without `pom.xml` files (i.e. 
outside of the SeqWare development directories).  Here we are working in the workflow-dev directory: 

    $ cd ~/workflow-dev
    $ mvn archetype:generate
    ...
    829: local -> com.github.seqware:seqware-archetype-decider (SeqWare Java Decider archetype)
    830: local -> com.github.seqware:seqware-archetype-java-workflow (SeqWare Java workflow archetype)
    831: local -> com.github.seqware:seqware-archetype-module (SeqWare module archetype)
    Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 294: 

<p class="warning"><strong>Note:</strong>You can inform Maven about the archetypes by downloading the latest <a href="https://github.com/SeqWare/seqware/releases/download/<%= seqware_release_version %>/archetype-catalog.xml">archetype-catalog.xml</a> file and placing it in <code>~/.m2/</code>.</p>

The numbers used to identify  the archetypes will vary 
depending on what you have installed, so you will need to scan through the list 
to find the SeqWare archetype you are looking for, in this case "SeqWare Java workflow archetype". You can also enter "seqware" as a filter in order to narrow down the possibilities. Following the prompts, use "com.github.seqware" as the package, "MyHelloWorld" as the artifactId and workflow-name, and 1.0 for the version.

Alternately, the archetype can be generated without any interaction:

    $ mvn archetype:generate \
      -DinteractiveMode=false \
      -DarchetypeCatalog=local \
      -DarchetypeGroupId=com.github.seqware \
      -DarchetypeArtifactId=seqware-archetype-java-workflow \
      -DgroupId=com.github.seqware \
      -DartifactId=workflow-MyHelloWorld \
      -Dworkflow-name=MyHelloWorld \
      -Dversion=1.0
