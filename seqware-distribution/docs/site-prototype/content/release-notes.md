---

title:     "Release Notes"
markdown:  basic

---

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
* SEQWARE-1004 - Add biojava dependencies to our artifactory

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


