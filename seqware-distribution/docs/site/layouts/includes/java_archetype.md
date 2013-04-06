The first step to get started is to generate your workflow skeleton using Maven 
archetypes. You will want to do this in a directory without pom.xml files (i.e. 
outside of the SeqWare development directories).  Here we are working in the provisioned-bundles directory: 
         
        cd /home/seqware/SeqWare 
        cd provisioned-bundles  
        mvn archetype:generate 
        ... 
        720: local -> com.github.seqware:seqware-archetype-decider (SeqWare Java Decider archetype) 
        721: local -> com.github.seqware:seqware-archetype-java-workflow (SeqWare Java workflow archetype) 
        722: local -> com.github.seqware:seqware-archetype-simplified-ftl-workflow (SeqWare FTL workflow archetype) 
        723: local -> com.github.seqware:seqware-archetype-module (SeqWare module archetype) 
        724: local -> com.github.seqware:seqware-archetype-legacy-ftl-workflow (SeqWare workflow legacy ftl archetype) 
        725: local -> com.github.seqware:seqware-archetype-simple-legacy-ftl-workflow (A very simple SeqWare legacy ftl workflow archetype) 
        # select 721 above, the "SeqWare Java workflow archetype" 
        # use HelloWorld as the name of your workflow and use the default workflow version 
        cd HelloWorld 
        mvn install 
        cd target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/ 
 
The numbers used to identify  the archetypes (720 through 725) will vary 
depending on what you have installed, so you will need to scan through the list 
to find the SeqWare archetype you are looking for, in this case "SeqWare Java workflow archetype". 
 
In this example, one would chose 721, the template for workflow using Java 
objects.  Then use "HelloWorld" as the artifactId.  You may wish to change the 
version number for your workflow but the defaults for the remaining questions 
should be fine. 
