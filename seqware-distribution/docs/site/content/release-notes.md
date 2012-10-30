---

title:     "Release Notes"
markdown:  basic

---

<!-- 
Procedure now is at https://wiki.oicr.on.ca/pages/viewpage.action?pageId=44533172
-->

## 0.13.4 (2012-10-22)

### Defect
* [SEQWARE-1294] - Portal Fails when creating a Sequencer Run

### Improvement
* [SEQWARE-1264] - 0.12.5.1 Release
* [SEQWARE-1265] - 0.13.3 Release

### Story
* [SEQWARE-779] - Annotation workflow to support the ICGC (duplicate of SEQWARE-1038)
* [SEQWARE-1154] - add xml support based on the current java objects for workflow
* [SEQWARE-1193] - improve the java workflow base on the new spec
* [SEQWARE-1243] - We need integration tests for each sub-project
* [SEQWARE-1250] - Make sure that attribute writeback works perfectly from the commandline and within workflows
* [SEQWARE-1251] - SOP for documentation release

### Task
* [SEQWARE-1291] - Announce 0.12.5.1 and 0.13.3 releases on twitter and group page after once-over as a group
* [SEQWARE-1292] - Complete release to pipedev.hpc

### Technical task
* [SEQWARE-619] - Command line cleanup
* [SEQWARE-640] - QE RESTful API documented in nanoc and on website
* [SEQWARE-978] - java workflow documentation
* [SEQWARE-1196] - modify current workflowObjectModels according to the spec
* [SEQWARE-1202] - Update the mvn archetypes and make available on github
* [SEQWARE-1213] - Update our OICR SDK, web services, database for 0.12.5.1
* [SEQWARE-1214] - Release 0.12.5.1 hotfix that includes BundleManager fixes
* [SEQWARE-1236] - clean up the workflow client
* [SEQWARE-1245] - We need a checklist from SeqProdBio and PipelineEval for integration tests to do before a release
* [SEQWARE-1268] - create setupDirectory() for dataModel
* [SEQWARE-1271] - check text/key-value for metadata saver and make sure that it works
* [SEQWARE-1273] - put workflow java doc in nanoc
* [SEQWARE-1274] - add classpath parameter to javaSeqwareModuleJob
* [SEQWARE-1276] - create a workflow client sample in VM that includes all features in the new simplifiled workflow
* [SEQWARE-1277] - discuss with Brian about the new workflow when it is ready
* [SEQWARE-1278] - change the workflow engine, allow it to take in parent workflowrun id for metadata wirteback
* [SEQWARE-1281] - merge comand line options with configs<string,string> in datamodel
* [SEQWARE-1295] - We need command line tools to populate sequencer_run, lane, and IUS
* [SEQWARE-1299] - update dax after discuss with Brian
* [SEQWARE-1306] - write a plugin to generate dot file from processing table
* [SEQWARE-1307] - Test Oozie to see if SeqWare Runner Bash tasks can be run via this workflow system on a Hadoop Cluster

## 0.13.3 (2012-10-09)

### Defect
* [SEQWARE-1204] - MetaDB procedure seems broken.

### Improvement
* [SEQWARE-1191] - Put a link to github on the SeqWare website

### Technical task
* [SEQWARE-186] - Convert SeqWare svn repository to git
* [SEQWARE-669] - Updated Workflow Bundle SDK
* [SEQWARE-1155] - create java objects that can serialize to xml/json
* [SEQWARE-1175] - add lib as the default classpath, and force all .jar go to lib
* [SEQWARE-1176] - add dependencies in xml
* [SEQWARE-1177] - Get BundleManager --test working with current version
* [SEQWARE-1178] - Release a 0.12.5 version
* [SEQWARE-1179] - Upload the Javadocs for the released 0.12.5 version to the github website
* [SEQWARE-1180] - Merge the sf.net pom.xml with the github pom.xml and ensure building works
* [SEQWARE-1181] - Redirect Jenkins to github, Jenkins now does findbugs and pmd
* [SEQWARE-1182] - Configure Query Engine tests for jenkins compatibility
* [SEQWARE-1187] - Test out JavaCC versus ANTLR
* [SEQWARE-1188] - Implement parse trees and RPNStack builder via chosen lexer/parser
* [SEQWARE-1189] - Adapt QueryVCFDumper  for query language
* [SEQWARE-1190] - Create plugin of FeatureFilters
* [SEQWARE-1194] - create a workflow template for the workflow author
* [SEQWARE-1195] - modify workflowlauncher to create seqwareWorkflowObjectModel
* [SEQWARE-1197] - implement vep sample using the latest java workflow
* [SEQWARE-1198] - finalize the spec for java workflow
* [SEQWARE-1199] - modify xml workflow based on the new java workflow
* [SEQWARE-1200] - move archetype-workflow-v2 to git feature branch
* [SEQWARE-1201] - add uncommited code from svn to git
* [SEQWARE-1206] - Our workflow development docs on internal wiki need to be updated for 0.12.5.1
* [SEQWARE-1208] - Fix metaDB creation
* [SEQWARE-1209] - Ensure that the overall combination of unit tests and integration tests work properly
* [SEQWARE-1211] - Investigate relationship between HubFlow release process and maven release script
* [SEQWARE-1215] - Revise simplified workflow bundle spec and code review with Yong
* [SEQWARE-1226] - Make an even simpler hello world workflow archetype
* [SEQWARE-1227] - Improve documentation  and prepare for demo
* [SEQWARE-1228] - Finish Hello World app
* [SEQWARE-1229] - remove the job.getArguments().add()
* [SEQWARE-1230] - change Job object to interface
* [SEQWARE-1231] - change package name (net.sourceforge.seqware.pipeline.workflowV2.pegasus)
* [SEQWARE-1232] - create archetype-java-workflow, archetype-ftl-workflow
* [SEQWARE-1233] - get/set gcr-skip-if-missing and other options
* [SEQWARE-1234] - remove WorkflowInfo object 
* [SEQWARE-1235] - move sw_cluster out of configs
* [SEQWARE-1238] - modify the code for simplified FTL->simplified XML->WorkflowDataModel based on the latest WorkflowDataModel

## 0.13.2 (2012-09-20)

###Task
* [SEQWARE-1164] - simplified workflow - auto dependencies

###Technical task
* [SEQWARE-287] - EC2 AMI
* [SEQWARE-498] - VirtualBox VM
* [SEQWARE-1086] - Create configuration file and parameter passing
* [SEQWARE-1115] - ensure that new syntax supports one of Morgan's new workflows
* [SEQWARE-1116] - ensure that our archetype that will produce one of Morgan's workflow that will use the new system, and launch
* [SEQWARE-1143] - Investigate VEP memory leak
* [SEQWARE-1144] - Report RetriesFailedWithDetails
* [SEQWARE-1145] - SeqWare Query Engine documentation
* [SEQWARE-1146] - Add ability to do tag and range queries from the dumper
* [SEQWARE-1147] - Debug query launch on machines without Hadoop
* [SEQWARE-1148] - Create tests for VCF Dumper (with tag and range queries)
* [SEQWARE-1157] - create a helloworld example for xml version
* [SEQWARE-1158] - try one of the Morgan's workflow in xml
* [SEQWARE-1160] - Upgrade VCFDumper with poster code for M/R
* [SEQWARE-1174] - Identifying and testing out parser/lexer

<!---
I did this via exporting to Excel from JIRA and then copying the appropriate columns, after removing OICR employee specific-tasks
We still have to manually delete the parent task IDs for technical tasks though. There should be a cleaner way.
-->

## 0.13.1 (2012-09-03)

###Defect
* SEQWARE-1140 - WorkflowLauncher doesn't use command line arguments when scheduling workflows

###Improvement
* SEQWARE-1141 - Allow WorkflowRunReporter to print to a user-specified file name or to stdout

###Story
* SEQWARE-414 - Empty entries in ini files for workflows should still go into workflow_param table
* SEQWARE-1121 - Poster for Genome Informatics

###Task
* SEQWARE-1127 - install nanoc in seqware dev
* SEQWARE-1142 - move sqwprod postgres data to nfs

###Technical Task
* SEQWARE-1088 - Create implementation of Friendly prefixes for TagSets (and other classes)
* SEQWARE-1091 - Create nested hash structures for Feature Tags
* SEQWARE-1118 - SO Synonym support
* SEQWARE-1120 - Prepare for DCC tutorial
* SEQWARE-1131 - Debug VCF File importer zipped files
* SEQWARE-1117 - structural changes (launching, monitoring, etc.) divergence ... needs to be merged into 2.0 of workflow
* SEQWARE-1021 - Record performance/test range/token filters
* SEQWARE-565 -	Add attribute support to ProvisionFiles and Generic Metadata Saver
* SEQWARE-1033 - VEP workflow: debug with metadata
* SEQWARE-1111 - Finish VCF parser with new QE parameters
* SEQWARE-1113 - Finish collating tagset between Annovar and VEP
* SEQWARE-1074 - add workflow attribute
* SEQWARE-1075 - add workflow run attribute
* SEQWARE-1114 - bulk import of attributes
* SEQWARE-1044 - Debug with metadata
* SEQWARE-1053 - WoW: Debug without metadata
* SEQWARE-1054 - WoW: debug with metadata
* SEQWARE-1106 - Install all necessary workflows on sqwstage
* SEQWARE-1098 - If Run is Skipped, Lanes and Iuses will be Automatically Skipped
* SEQWARE-1122 - Create Introduction
* SEQWARE-1123 - Outline and finish methods
* SEQWARE-1124 - Experiment and Get Statistics for a ICGC Load
* SEQWARE-1125 - Map/Reduce statistics collection with in-memory comparison
* SEQWARE-1126 - Programmatic Access and Acknowledgements

## 0.13.0 (2012-08-20)

###Story
* SEQWARE-97 - Add plugin architecture for deciders, move from Perl to Java
* SEQWARE-1037 - Get the cluster and hboot talking

###Task
* SEQWARE-1015 - Web service users need access to a report showing status information from both LIMS and SeqWare
* SEQWARE-1077 - Webservice issues

###Technical task 
* SEQWARE-1017 - Deal with Unknown Scanner Exception bug
* SEQWARE-1019 - Catch-up specification for Query Engine
* SEQWARE-1020 - Refactor plug-ins, implement install of plug-ins via classpath
* SEQWARE-1023 - Implement loader with SO term support
* SEQWARE-1024 - Update command-line loader with SO term support
* SEQWARE-1064 - Debug existing map/reduce tasks on the cluster
* SEQWARE-1065 - Implement tests for SO integration
* SEQWARE-1066 - Add command-line tool doc for Morgan by End of Week
* SEQWARE-1067 - Convert Java logging to Log4j
* SEQWARE-175 - Convert workflow launching script into Java Plugin
* SEQWARE-1089 - Convert workflow status to Java Plugin
* SEQWARE-839 - Ensure install process works via command line and web service
* SEQWARE-340 - Allow pushing of workflows by S3 upload
* SEQWARE-342 - Allow pulling of files from mutually readable file system
* SEQWARE-343 - Code install process through web service
* SEQWARE-1032 - VEP workflow: debug without metadata
* SEQWARE-1034 - VEP workflow: write spec for VCF file parsing
* SEQWARE-1035 - VEP workflow: write script for VCF file parsing
* SEQWARE-1026 - bug fix for experiment attribute
* SEQWARE-1068 - add file attribute
* SEQWARE-1069 - add ius attribute
* SEQWARE-1070 - add lane attribute
* SEQWARE-1071 - add processing attribute
* SEQWARE-1073 - add study attribute
* SEQWARE-1079 - add sample attribute
* SEQWARE-1084 - try attribute annotation command line to make sure that all attribute tables work
* SEQWARE-1090 - change the xml output of all the attribute tables
* SEQWARE-1040 - Install QE cmd line tools locally
* SEQWARE-1041 - Create bundle and import into git
* SEQWARE-1042 - Implement jobs
* SEQWARE-1043 - Debug without metadata
* SEQWARE-1059 - Install dependencies into artifactory
* SEQWARE-1060 - Test the connection
* SEQWARE-1045 - WoW: create bundle and import into git
* SEQWARE-1048 - WoW: implement the jobs (get the hint from Brian about how to execute processes locally)

## 0.12.5 (2012-08-06)

###Improvement
* SEQWARE-955 - hboot.res
* SEQWARE-969 - Implement HighCapacity FeatureSets
* SEQWARE-975 - Create and Tag Release
* SEQWARE-1004 - Add guava dependencies to our artifactory

###Story
* SEQWARE-555 - Design a Framework for Deciders
* SEQWARE-804 - VM for SeqWare (Virtual Box)
* SEQWARE-985 - ProvisionFiles needs better error checking

###Task
* SEQWARE-1014 - workflow installation error

###Technical task

* SEQWARE-710 - Implement queries for: query by tags
* SEQWARE-712 - Implement queries for: overlap queries
* SEQWARE-713 - Implement queries for: intersect queries
* SEQWARE-752 - Create in-memory back-end implementation and tests for plugin interface and FeatureStoreInterface
* SEQWARE-941 - Fix headless workflow error with GATK and Variant annotation
* SEQWARE-960 - Test error propagation in real workflows
* SEQWARE-965 - Ensure correct dependencies for jar, runnable command-line tools
* SEQWARE-966 - Refactoring for presentation
* SEQWARE-967 - Complete Javadoc
* SEQWARE-968 - Complete New Query Engine Developer Guide
* SEQWARE-970 - Explicitly implement multiple Features in a location using the existing SGID
* SEQWARE-971 - Implement tests for multiple Features in a location
* SEQWARE-972 - Implement HighCapacityFeatureSet class
* SEQWARE-973 - Optimize TableScanning calls to take advantage of new classes
* SEQWARE-974 - Upgrade command-line utilities to new API
* SEQWARE-1001 - Expose serialization versioning to the user-level
* SEQWARE-1002 - Improve encrypt/decryption
* SEQWARE-1003 - Improvements to ln -s error checking


