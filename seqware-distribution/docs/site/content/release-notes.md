---

title:     "Release Notes"
markdown:  basic

---

<!-- 
Procedure now is at https://wiki.oicr.on.ca/pages/viewpage.action?pageId=44533172
-->


## 1.1.0 (2015-03-12)

This release primarily captures the state of SeqWare before deployment in production for OICR GSI (Genome Sequencing Informatics). 

## Defect
* [SEQWARE-2017] - BasicDecider --force-run-all versus --ignore-previous-runs

## Improvement
* [SEQWARE-2016] - Ability to specify parent/input file accession(s) to seqware create workflow-run

## 1.1.0-rc.0 (2015-02-17)

This release primarily captures the state of SeqWare before deployment in the staging environment for OICR GSI (Genome Sequencing Informatics). 

### Defect
* [SEQWARE-2007] - command "seqware workflow-run ini" fails if "--out" not specified

### Story
* [SEQWARE-2012] - Oozie job name length redux

## 1.1.0-beta.1 (2015-01-16)

A minor release implementing a few fixes and updates needed for PDE integration testing. 

### Defect
* [SEQWARE-2002] - Could not link workflow run to its parents
* [SEQWARE-2005] - Workflow job names must be 50 characters or less (oozie restriction)

### Improvement
* [SEQWARE-2003] - OicrDecider handling of trailing delimiter in parent sample name and parent swid fields

## 1.1.0-beta.0 (2014-12-23)

This is a major release implementing all remaining features for the 1.1.0 beta release. Following releases in this series will be limited to hotfixes. 

New features include updates to the file provenance report to include workflow and workflow run annotations, aggregate skip information across all paths to a particular file, and report input file information. A new reporting resource displays JDBC and version information for the web service, file provisioning for workflows now records and checks file size and md5sum, it is now possible to pull back arbitrary entries and annotations on the CLI, and it is now possible for workflows to annotate processing events and files while a workflow is running. 

### Defect
* [SEQWARE-1993] - When using inmemory metadata method, bundle dry-run fails to execute

### Improvement
* [SEQWARE-1889] - Accessing files linked to lanes
* [SEQWARE-1981] - Include workflow run input files into the file provenance report?
* [SEQWARE-1985] - Get version of SeqWare webservice

### Task
* [SEQWARE-1956] - GCR keeps retrying a lock when the filesystem is down
* [SEQWARE-1959] - Create general mechanism to run annotator in workflows
* [SEQWARE-1970] - validate seqware version between CLI and WS
* [SEQWARE-1974] - Provision files module should record file size and md5 checksum
* [SEQWARE-1975] - Store file size and md5sum in the seqware database
* [SEQWARE-1980] - Fix dry-run option on CLI
* [SEQWARE-1991] - Pull back annotation for arbitrary sw_accession
* [SEQWARE-1992] - Make job id on sge cluster unique
* [SEQWARE-1994] - When a file with multiple paths has a path skipped, the file should be skipped


## 1.1.0-alpha.5 (2014-10-23)

This is a minor release re-enabling a deprecated web-resource for the pan-cancer project. Specifically, pulling back workflow runs by workflow. 

## 1.1.0-alpha.4 (2014-10-09)

This release is a minor release largely focused on technical debt and monitoring of code quality. Support for Cobertura and Jacoco has been added, findbugs has been integrated into the build process to reject high priority bugs (with all existing high priority bugs now fixed), maven plugin versions have been updated, commons-lang has been replaced with commons-lang3, and apache-collections has been replaced with apache-collections4. It is now also possible to schedule to 1.0.15 clusters with 1.1 series decider code. 

## 1.1.0-alpha.3 (2014-09-17)

This release is a minor release to workflow developers in order to demo and get feedback on a prototype for interpreted language deciders.

There are also a number of Oozie fixes that and seqware-bag fixes that await their own releases in their respective projects. 

### Improvement
* [SEQWARE-1429] - Expose Workflow .ini parameters as Decider usage flags

### Task
* [SEQWARE-1944] - Oozie should kill SGE jobs when they go into Eqw state
* [SEQWARE-1962] - Prototype Python ReferenceDecider
* [SEQWARE-1963] - seqware-bag should set oozie log4j parameter
* [SEQWARE-1964] - Implement encryption for seqware-bag
* [SEQWARE-1965] - oozie-sge plugin appears to report incorrect qstat
* [SEQWARE-1967] - Add retry documentation and remove outdated documentation

## 1.1.0-alpha.2 (2014-09-08)

This release focuses on usability improvements. Highlights include a workaround for the retry utility to workaround a known issue with Oozie, an update to spring and restlet, the ability to run workflows via whitestar without a web service and without a DB, tools for bulk transition of workflows (cancel and retry), re-scheduling workflows, and a re-working of GCR to save full output to disk.   

A few new utilities and bugfixes have also been added to aid in workflow development. 

### Defect
* [SEQWARE-738] - when there are identically named studies in the DB the web service only shows one
* [SEQWARE-1933] - Bundle install doesn't report an error when no SW_BUNDLE_DIR is set
* [SEQWARE-1934] - seqware bundle package updates the timestamp on existing zips but not the files inside
* [SEQWARE-1938] - FileLinker path collision doesn't completely fail

### Improvement
* [SEQWARE-1242] - Extend and improve integration testing
* [SEQWARE-1923] - Developer Tutorial Documentation: maven parameters
* [SEQWARE-1957] - Call AbstractWorkflowDataModel.wrapup()

### Task
* [SEQWARE-1888] - Create tool to shutdown/restart all running workflows
* [SEQWARE-1891] - workflow status checker doesn't fail workflows (oozie suspends jobs when qmaster is unreachable)
* [SEQWARE-1925] - Make workflow-version available as a SeqWare variable
* [SEQWARE-1931] - Add pipefail to generated scripts
* [SEQWARE-1932] - Migrate jenkins to Bindle 1.2.1 and IceHouse
* [SEQWARE-1945] - Non-descriptive error message if you annotate twice with the same key and value
* [SEQWARE-1949] - Allow whitestar to run without metadata for production environments without a WS and db
* [SEQWARE-1950] - Create mapping utilities
* [SEQWARE-1951] - Annotate optional parameters 
* [SEQWARE-1952] - Prototype file provenance grouping and query utility
* [SEQWARE-1953] - Implement workaround for oozie-1879 retry issue 
* [SEQWARE-1954] - Implement re-schedule functionality 
* [SEQWARE-1955] - GCR output should still save full output on disk
* [SEQWARE-1958] - Add ability to override memory setting for control nodes
* [SEQWARE-1960] - BWA 2.6.0 workflow freezes on AWS
* [SEQWARE-1961] - Address seqware-bag (bindle) issues encountered during alpha deployment


### Technical task
* [SEQWARE-1417] - Create nanoc documentation for the ProcessingDataStructure2Dot
* [SEQWARE-1422] - Create integration tests for the ProcessingDataStructure2Dot


## 1.1.0-alpha.1 (2014-08-11)

This release refactors much of the code and removes many of the features that were deprecated in 1.0.0. 
A couple of new workflow engines, 'whitestar' and 'whitestar-sge', are provided for development and debugging without oozie + sge and oozie respectively. 

Significant refactoring of the plugins backing our CLI was done, splitting out the functionality in WorkflowLauncher into a (Scheduled)WorkflowLauncher, WorkflowWatcher, WorkflowScheduler, and WorkflowLifeCycle (which mimics our old WorkflowLauncher). Unused .seqware/settings parameters and plugin parameters have been removed and ini parsing has been consolidated to schedule-time only. 

We have also introduced coding standards and a host of fixes for workflow developers.

Deprecated utilities removed includes:

* SequencerRunReporter
* SymLinkFileReporter (replaced by File Provenance Reporter)

Database tables removed include:

* all x_link tables 
* all shareX tables except for study and workflow run
* sample_search

Web resources:

* webservice DTO classes
* asychronous classes for scheduling and running workflows (Pegasus)
* web-resources that back SymLinkFileReporter (reporting files associated with studies, sequencer runs, samples, etc.)
* web resources that back SequencerRunReporter
* IUSsearch, "x" libraries resources, and excess file attributes services

We have also removed support for the old workflow engine meaning we have dropped support for Pegasus, Condor, and Globus. 

### Improvement
* [SEQWARE-1797] - Create integration tests for BasicDecider based on tutorial and hook them in
* [SEQWARE-1882] - More informative error output in seqware bundle launch
* [SEQWARE-1902] - Retrieve the effective INI for a workflow-run in the CLI

### Task
* [SEQWARE-1604] - Make output from BundleManager --test WorkflowLauncher consistent
* [SEQWARE-1635] - Documentation/tutorial: Breaking workflows
* [SEQWARE-1645] - using job.addFile() together with setting a file as an output create redundant db records
* [SEQWARE-1801] - Workflow status for failed workflow run is marked as "Completed" (when using --wait)
* [SEQWARE-1840] - install-dir-only option for io.seqware.cli
* [SEQWARE-1858] - Refactor WorkflowLauncher for consistency
* [SEQWARE-1869] - Generate ini file with workflows with no parameters may crash
* [SEQWARE-1890] - Creating jobs out of order causes strange NPE
* [SEQWARE-1906] - Non-deterministic failure in tests
* [SEQWARE-1927] - Clean-up legacy Pegasus code
* [SEQWARE-1929] - Remove the symlink file reporter
* [SEQWARE-1930] - Remove unused database tables and associated Hibernate code
* [SEQWARE-1936] - Remove global ReturnValue antipattern from throughout SeqWare
* [SEQWARE-1937] - BasicDecider cannot find files injected using the CLI
* [SEQWARE-1939] - Create a controlled ENUM of all possible .seqware/settings keys
* [SEQWARE-1943] - seqware check is missing commons exec dependency
* [SEQWARE-1946] - Auto-generate listing of .seqware/settings variables from new enum
* [SEQWARE-1947] - Create a simple workflow engine to test our abstractions

### Technical task
* [SEQWARE-549] - Coding standards
* [SEQWARE-1456] - Investigate rapidly growing jar size
* [SEQWARE-1466] - Create nanoc documentation WorkflowLauncher
* [SEQWARE-1467] - Create nanoc documentation WorkflowStatusChecker
* [SEQWARE-1468] - Create integration tests for WorkflowLauncher
* [SEQWARE-1469] - Create integration tests for WorkflowStatusChecker
* [SEQWARE-1550] - FileAttributeResources fixes


## 1.0.18 (2014-07-18)

This release contains a hotfix for file import via the CLI and associated tutorials. 

### Improvement
* [SEQWARE-1937] -  BasicDecider cannot find files injected using the CLI 

## 1.0.17 (2014-07-07)

This release contains changes in order to address mainly issues for workflow/decider developers.
 
### Improvement
* [SEQWARE-1883] - Provision file module optimization
* [SEQWARE-1921] - Add sorting of attributes in file provenance report

### Task
* [SEQWARE-1907] - Add seqware script and archetype catalog via github release API
* [SEQWARE-1916] - Investigate issue with null parent in sample_hierarchy via JPA 
* [SEQWARE-1918] - BasicDecider --rerun-max cannot be set as 0
* [SEQWARE-1928] - BasicDecider: change --force-run-all to --ignore-previous-runs and fix finalCheck behaviour


## 1.0.16 (2014-06-17)

This release contains changes in order to address two main areas in addition to various usability improvements. First, the Java workflow API and the GCR have been improved in order to control of the amount of stdout and stderr that is stored in the metadb. Second, various ini issues having to do with the installation and launching of workflows have been addressed. 

### Defect
* [SEQWARE-1897] - A workflow run with missing provisioned out files completed successfully
* [SEQWARE-1909] - Error launching workflow run when workflow has a parameter with a default blank value

### Improvement
* [SEQWARE-1884] - Expose ProvisionFiles (module) "skip-if-missing" in seqware java api
* [SEQWARE-1901] - INI file shouldn't be required when scheduling workflows

### Task
* [SEQWARE-1758] - Buffer last 1000 lines of output in successful jobs
* [SEQWARE-1886] - Integrate Seqware Check tool into seqware bash script
* [SEQWARE-1892] - Add date to daemon output
* [SEQWARE-1893] - Workflow run update fails when std out/err has special characters
* [SEQWARE-1895] - Generic command runner out out memory error when large amount of standard error output
* [SEQWARE-1904] - Java workflows can't have private classes
* [SEQWARE-1911] - Can't escape = in SeqWare INI files
* [SEQWARE-1913] - End generated scripts with a newline already
* [SEQWARE-1915] - Expose stdout/stderr line control to Java API


## 1.0.15 (2014-05-05)

This release contained only one change in order to explicitly clean-up temporary tables in Postgres that back the file provenance report. 

## 1.0.14 (2014-04-21)

This release contains mostly usability improvements. New features include the ability to target deciders by root samples and a sanity check tool that can run some basic checks to diagnose issues with SeqWare installs.

### Defect
* [SEQWARE-1880] - Seqware pipeline failure on sample wildcard with a large number of children

### Improvement
* [SEQWARE-1871] - Seqware compression control

### Task
* [SEQWARE-1794] - Sanity Check Tool
* [SEQWARE-1827] - Create VirtualBox VM and update website with it
* [SEQWARE-1864] - Expose --verbose in the CLI
* [SEQWARE-1873] - Migrate (seqware jobs on) jenkins to CloudBindle
* [SEQWARE-1878] - Hook up javadoc generation into mvn site-deploy

### Technical task
* [SEQWARE-1798] - verify oozie availability, able to write files to hdfs, connectivity
* [SEQWARE-1799] - go through some checks from sqwref.hpc setup 

## 1.0.13 (2014-04-03)

This release contains mostly usability improvements. 
Core fixes include a fix for batched file provision events attached to jobs and the introduction of order to columns in the file provenance report relating to sample parents. 

### Defect
* [SEQWARE-1372] - Emails in the registration table cannot have capital letters
* [SEQWARE-1867] - Multiple values are delimited with the same key-value pair delimiter in the file provenance report
* [SEQWARE-1874] - Inconsistent ordering of sample parent names in file provenance report

### Improvement
* [SEQWARE-1254] - Add new attributes into the file report
* [SEQWARE-1431] - Tool to clean up installed workflows
* [SEQWARE-1857] - Additional file-provenance report filter parameters

### Task
* [SEQWARE-789] - clean up processing table, remove all the orphan processing rows
* [SEQWARE-1631] - Java workflows: Print a warning when more than one file is registered to the same name
* [SEQWARE-1866] - Write descriptions for plugins and a plugin that parses this information
* [SEQWARE-1868] - BasicDecider parent workflow accession filtering should be applied at file report level


## 1.0.12 (2014-03-19)

Note that the fix for 1860 creates an update to the oozie-sge plugin (version 1.0.2). The fix for SEQWARE-1848 may also require some special care, previous versions of the 1.0.X workflow launcher would run steps in a workflow using system Java. 1.0.12 enforces that the bundled Java is used; this will cause problems for workflows bundled with 1.0.11 so either launch those with 1.0.11 or rebundle with Java 1.7. Older bundles should be unaffected.  

Other changes are largely self-explanatory and generally improve usability.  

### Defect
* [SEQWARE-1382] - Check to see if the SW_BUNDLE_DIR is writeable
* [SEQWARE-1847] - Admin Deletion tool: Optimistic Lock Exception / no version number in the identity map 
* [SEQWARE-1848] - GenericCommandRunner is not executed with the workflow's bundled java

### Task
* [SEQWARE-1441] - Get more workflow information from command line
* [SEQWARE-1596] - obscure NullPointer exception when getProperty() hits a non-existing parameter
* [SEQWARE-1696] - Installing a bundle using symlink
* [SEQWARE-1845] - ProvisionFiles doesn't fail and return an appropriate return value/message when an inappropriate input-file-metadata is given
* [SEQWARE-1860] - Add sudo for "seqware workflow-run cancel"/qdel

### Technical task
* [SEQWARE-1657] - Determine procedure for dealing with partially completed workflows

## 0.13.6.16 (2014-03-19)
* Introduces a client timeout and exception catching for low-level calls to the web service
* Performance improvement for authentication checking in the processing hierarchy
* Adding verbose mode to the module runner

## 1.0.11 (2014-02-14)
### Task
* Updating SeqWare to build and test against Java 7 and Tomcat7
* Automatically batch file provision jobs above a threshold of OOZIE_BATCH_THRESHOLD into batches of OOZIE_BATCH_SIZE
* Allow workflow authors to manually batch BashJobs into batches
* [SEQWARE-1811] - Some dependencies aren't available when building seqware-admin-webservice
* [SEQWARE-1837] - Fix workflow filtering for workflow step time report

## 1.0.10 (2014-01-27)

### Improvement
* [SEQWARE-1803] - SeqWare 1.0 support for specifying workflow parameters from command line

## 1.0.9 (2014-01-07)

### Defect
* [SEQWARE-1807] - Oozie workflow jobs/actions do not relaunch - workflow fails

### Improvement
* [SEQWARE-1743] - Uninformative exceptions in Java workflow building

### Story
* [SEQWARE-1770] - Installed workflows have incorrect permissions on exectuables

### Task
* [SEQWARE-1817] - oozie development environments should use postgres
* [SEQWARE-1819] - .seqware/settings opened too many times on workflow launch with large workflows

## 0.13.6.15 (2014-01-07)
* BasicDecider includes a compatibility mode to run against legacy 0.13.6.5 web services

## 1.0.8 (2013-12-12)
### Defect
* [SEQWARE-1783] - SeqWare Admin WS does not create SeqWare Accessions
* [SEQWARE-1804] - SqwFile parent accessions do not get passed to Command Runner

### Task
* [SEQWARE-1766] - CLI: State where stdout and stderr are saved to
* [SEQWARE-1788] - SymlinkReporter does not report correct infomation for files provisioned out with setParentAccessions
* [SEQWARE-1790] - Migrate the Oozie-SGE code to SeqWare
* [SEQWARE-1791] - Get vagrant provisioning of multiple SGE nodes working
* [SEQWARE-1793] - Implement a replacement for the symlinkreporter based on the file-provenance-report
* [SEQWARE-1802] - File records missing in file provenance report
* [SEQWARE-1805] - BasicDecider adding support for filtering by barcode (IUS) and lane
* [SEQWARE-1808] - Ensure that BasicDecider only uses input from completed workflow runs

### Technical task
* [SEQWARE-1670] - SymLinker doesn't report all connections to GATK runs
* [SEQWARE-1704] - seqware pipeline: symlinkreporter should error out if any ws requests fail in dump all mode
* [SEQWARE-1762] - skipped sequencer runs are still shown in the symlink reporter
* [SEQWARE-1763] - missing links between workflow run and IUS for GATKAnnotateAndSort and SomaticClassifier workflow
* [SEQWARE-1779] - Missing CLI functionality in seqware files report
* [SEQWARE-1795] - Plugin to run through CLI and pre-CLI tutorials
* [SEQWARE-1796] - Create tool to verify that db settings (if present) match web service

## 0.13.6.14 (2013-12-12)
* BasicDecider now uses only input files from completed workflow runs
* Disabled linking behaviour between provision out steps which caused large numbers of provision out steps to operate in series rather than in parallel 

## 1.0.7 (2013-11-13)

### Defect
* [SEQWARE-1707] - Workflow runs can be scheduled using invalid INI files
* [SEQWARE-1764] - Memory leak in JAXB Unmarshalling
* [SEQWARE-1782] - Convert filechild query params to json, also limit results by relevant workflow runs 


### Story
* [SEQWARE-1539] - Tool to skip file entry and delete file on disk
* [SEQWARE-1681] - As developers we need to be able to create VMs (VirtualBox, OpenStack, and Amazon) on demand using Vagrant

### Task
* [SEQWARE-1346] - Fully implement "delete" attribute (formerly: add skip column to file table)
* [SEQWARE-1515] - workflow_run fail and file deletion
* [SEQWARE-1746] - Deployment - Setup sqwuser2.hpc for PDE to develop 1.0.x workflows on
* [SEQWARE-1747] - Documentation - make sure our internal and external docs are 1.0.x compatible
* [SEQWARE-1778] - WorkflowLauncher does not record workflow parameters
* [SEQWARE-1780] - Safety measure, deletion key
* [SEQWARE-1781] - CheckDB - link back to web resources
* [SEQWARE-1784] - FileLinker should not require Lane

### Technical task
* [SEQWARE-1542] - Standalone deletion infrastructure
* [SEQWARE-1589] - Design File Provisioning and Reserved Variables in SeqWare Pipeline
* [SEQWARE-1689] - output_prefix and output_dir are not handled correctly according to our docs
* [SEQWARE-1715] - Figure out when "Magic" variables are used and available
* [SEQWARE-1729] - Vagrant cluster launching on OS, AWS, and VirtualBox
* [SEQWARE-1732] - Allow users to determine lock file for WorkflowStatusChecker
* [SEQWARE-1749] - Release a 1.0.x VirtualBox VM and AMI
* [SEQWARE-1386] - Refactor REST interface into separate project and war

## 0.13.6.13 (2013-11-13)

* [SEQWARE-1773] - Study-wide MetadataWS searches fail if there are 1000+ entries
* [SEQWARE-1750] - Workflow run relaunches indefinently

## 1.0.5 (2013-10-01)

### Improvement
* [SEQWARE-1647] - Java workflows: provisioned files are not written to directories with random numbers

### Technical task
* [SEQWARE-1588] - Add output checking to WorkflowRunReporter and SymLinkReporter steps of user tutorial ITs
* [SEQWARE-1760] - Orphan Checking Plugin
* [SEQWARE-1761] - WorkflowRun Conventions

## 0.13.6.11 (2013-09-27)
* [SEQWARE-1748] - Calling WorkflowLauncher with multiple parent-accession results in error

## 1.0.4 (2013-09-20)

###Defect

* [SEQWARE-1574] - WebService - Clear up the connection pool mess
* [SEQWARE-1699] - NullPointerException when launching a scheduled workflow
* [SEQWARE-1722] - Oozie engine does not emit all GCR flags
* [SEQWARE-1750] - Workflow run relaunches indefinently

### Improvement

* [SEQWARE-1622] - Have BasicDecider check to see if the files exist before launching
* [SEQWARE-1726] - SeqWare MetaDB - Generate docs for the schema and upload to our seqware.io site

### Story

* [SEQWARE-137] - need to add workflow to X tables in the db for study, experiment, sample, etc and allow for linking via the WorfklowLauncher
* [SEQWARE-1625] - BasicDecider improvements
* [SEQWARE-1733] - Clean-up metadata tools

### Task

* [SEQWARE-1433] - WorkflowLauncher needs to set the host regardless of the launching method
* [SEQWARE-1512] - Cleanup DBAccess
* [SEQWARE-1513] - Modify the Maven archetype for decider jars to follow the PDE syntax (matches the workflow)
* [SEQWARE-1580] - WorkflowStatusChecker can short-circuit evaluation
* [SEQWARE-1593] - WorkflowStatusChecker can only run for single workflow-run-accession
* [SEQWARE-1605] - Pipeline - Create a generic methods HTML report for each workflow_run
* [SEQWARE-1706] - Add skip attribute to Old StudyReporter
* [SEQWARE-1731] - Work with HelpDesk to create Jenkins back-up procedure
* [SEQWARE-1744] - FileLinker plugin

### Technical task

* [SEQWARE-473] - Pipeline JAR should know what version of the webservice it is compatible with
* [SEQWARE-616] - Decide which File Injection Tools should exist
* [SEQWARE-1241] - Figure out a way to use archetypes without having to checkout and install the SeqWare git repo
* [SEQWARE-1547] - Check if ID is an integer to avoid NumberFormatException
* [SEQWARE-1548] - Check for potential NullPointers
* [SEQWARE-1549] - Check that JaxbObject handles nulls properly
* [SEQWARE-1616] - Code review of workflow engines and action executors with Alex
* [SEQWARE-1626] - Have the BasicDecider print out the defaults for each command-line option
* [SEQWARE-1655] - Add an option for running in reverse chronological order
* [SEQWARE-1712] - Implement system monitoring tools in Portal or other location
* [SEQWARE-1734] - Update FileLinker to use WS
* [SEQWARE-1736] - Create tests for FileLinker
* [SEQWARE-1737] - Deprecate GenericMetadataSaver
* [SEQWARE-1738] - Delete BulkProvisionFiles and ProcessingTaskGroup



## 0.13.6.10 (2013-08-21)
* Reduce the amount of temporary file space used by the basic decider 

## 1.0.3 (2013-08-21)

### Defect

* [SEQWARE-1287] - WorkflowLauncher should mark finished workflows as "completed" and not "success"
* [SEQWARE-1374] - Metadata does not check for valid id links
* [SEQWARE-1690] - SeqWare Pipeline: does the PegasusEngine not include random in the workflow name for Java workflows? Without this Pegasus can overwrite working directories when run concurrently!
* [SEQWARE-1693] - BasicDecider incorrectly skips launching of workflow runs when input files share a common workflow run id

### Improvement

* [SEQWARE-851] - improvement of workflow run report
* [SEQWARE-1328] - Remove accession from the Metadata object for the study table

### Story

* [SEQWARE-1400] - Oozie engines need to be supported transparently by all our command line utils (status checker, workflow launcher, etc)
* [SEQWARE-1564] - Cmd Line Tools - rework command line interface to work more like the hadoop command; easier, better docs, less typing
* [SEQWARE-1586] - ProvisionFiles - support HTTP, SFTP, SCP, etc to allow for easy pulling and pushing of data between systems

### Task

* [SEQWARE-1489] - Number of SequencerRuns retrieved by the WS is doubled.
* [SEQWARE-1544] - Create integration tests for SeqWare generic decider from archetype
* [SEQWARE-1561] - Library selection, library source and library strategy are not inserted when a lane is created
* [SEQWARE-1695] - Test BioNimbus
* [SEQWARE-1697] - BundleManager does not test properly (passes test without waiting)
* [SEQWARE-1700] - BundleManager list params fails on FileImport (possibly workflows with no parameters)
* [SEQWARE-1705] - AttributeAnnotator file skip default value should be false
* [SEQWARE-1727] - Investigate discovered potential issue with processing links

### Sub-task

* [SEQWARE-1639] - Review the documentation comments and re-work our docs accordingly

### Technical task

* [SEQWARE-595] - populate workflow_run_param table when use command line tools
* [SEQWARE-1138] - WorkflowLauncher: when template parsing fails, workflowlauncher --launch-scheduled does not set the run to failed
* [SEQWARE-1490] - BUG - issue with multiple workflow output files and Oozie backend
* [SEQWARE-1554] - Pipeline - Need command line, web service, and StatusChecker support for cancelling workflows
* [SEQWARE-1563] - Pipeline - some sort of metadata/workflow report tool to generate methods documents & record of metadata
* [SEQWARE-1570] - Investigate how to use plugin with --new without --interactive.
* [SEQWARE-1579] - Fix the way the sample hierarchy injection works.
* [SEQWARE-1584] - SeqWare command line Metadata plugin needs full lifecycle... e.g. query, create, and (possibly) update/delete.
* [SEQWARE-1624] - Update the integration tests to match tutorials
* [SEQWARE-1628] - Implement and test an Oozie -> SGE bridge in the Oozie workflow engine
* [SEQWARE-1682] - Add integration tests for retrieval of annotations from new report
* [SEQWARE-1688] - AttributeAnnotator should output list of attribute changes
* [SEQWARE-1701] - Job default thread/memory in seqware settings
* [SEQWARE-1702] - Stderr/Stdout propagation in the Oozie-Hadoop engine (Alex will work on the Oozie-SGE engine)
* [SEQWARE-1703] - Workflow cancel/restart state propagation
* [SEQWARE-1708] - Improve vagrant launcher with software profiles support
* [SEQWARE-1709] - Improve vagrant launcher to include cluster support
* [SEQWARE-1710] - Adapt the integration tests for our tutorials to use the CLI
* [SEQWARE-1713] - MetadataWS experiment spot_design_id and library_design_id support
* [SEQWARE-1714] - MetadataWS sequencer_run status support
* [SEQWARE-1717] - Create integration tests for the Metadata plugin
* [SEQWARE-1723] - Database build scripts should populate extra tables needed for setting experiment fields
* [SEQWARE-1724] - Multiple root/parent nodes inserted into sample_heirarchy

## 0.13.6.9 (2013-08-10)
* Fixes to the logic of the BasicDecider

## 1.0.2 (2013-08-10)

### Defect

* [SEQWARE-1668] - GenericCommandRunner - Morgan reported broken --stdout and --stderr redirects in this tool

### Improvement

* [SEQWARE-1508] - Alarming Not Found message in decider output
* [SEQWARE-1675] - BasicDecider is very slow
* [SEQWARE-1683] - We need a SW_DEFAULT_WORKFLOW_ENGINE parameter that can be set in the .seqware/settinngs file

### Task

* [SEQWARE-1527] - WorkflowRunReporter needs to print stdout and stderr in separate commands
* [SEQWARE-1612] - Seqware BasicDecider Testing mode
* [SEQWARE-1642] - Refine the BasicDecider --test output
* [SEQWARE-1666] - BasicDecider --launch-max
* [SEQWARE-1669] - Generic Command Runner only produces output when it fails
* [SEQWARE-1673] - The twitter feed in our docs is broken
* [SEQWARE-1691] - Update .seqware/settings file with new params
* [SEQWARE-1692] - New basic workflow creation tools (bugs)

### Technical task

* [SEQWARE-1269] - generic command runner should optionally take commands from a file
* [SEQWARE-1304] - Add documentation for new Web Service resources (sequencer run, ius, lane)
* [SEQWARE-1340] - Assorted ideas for plugin integration testing
* [SEQWARE-1415] - Document AttributeAnnotator for nanoc
* [SEQWARE-1418] - Create nanoc documentation for the WorkflowRunReporter
* [SEQWARE-1420] - Create integration tests for the AttributeAnnotator
* [SEQWARE-1423] - Create integration tests for the WorkflowRunReporter
* [SEQWARE-1606] - Change VM startup script to auto-create "emphemeral drives" if they aren't available
* [SEQWARE-1633] - Specification for Oozie SGE Submitter and associated components
* [SEQWARE-1664] - Setup our query integration tests to work against the HBase installed by Vagrant
* [SEQWARE-1698] - Successfully run extended integration tests on Vagrant VM

## 0.13.6.8 (2013-07-10)
* Implementation of new tools for wrapping external scripts with SeqWare metadata write-back
* Database indexing scripts optimizing against common queries and our various utilities

## 1.0.1 (2013-07-10)

### Defect
* [SEQWARE-1516] - NullPointer when trying to test installed workflows

### Improvement
* [SEQWARE-1632] - --install-dir-only and --install should be exclusive options

### Task
* [SEQWARE-1621] - Get oozie working on the AMI
* [SEQWARE-1660] - Support for external user : BD
* [SEQWARE-1674] - The workflowrun report can throw a null pointer exception on valid params

### Sub-task
* [SEQWARE-1637] - Document pom.xml file links for workflow bundles

###Technical task
* [SEQWARE-547] - Comments and javadoc coverage statistics
* [SEQWARE-548] - JUnit coverage
* [SEQWARE-585] - Run through Chef, Puppet, Whirr or other tools for provisioning cloud instances
* [SEQWARE-1225] - The build test suite should both create and remove the test metadb
* [SEQWARE-1460] - Integrate either GetSatisfaction or Disqus into GitHub website
* [SEQWARE-1488] - Handle build busy-work automatically
* [SEQWARE-1506] - Create and release source jars that can automatically be pulled down by IDEs
* [SEQWARE-1560] - Configurations - make sure we consistently handle magic variables like ${workflow_bundle_dir}
* [SEQWARE-1614] - ext-testing against arbitrary host
* [SEQWARE-1677] - AttributeAnnotator does not reject skipping unskippable tables
* [SEQWARE-1678] - AttributeAnnotator claims success with invalid parameters
* [SEQWARE-1679] - AttributeAnnotator unable to annotate Processing
* [SEQWARE-1680] - AttributeAnnotator cannot re-annotate Sample and Lane
* [SEQWARE-1685] - Implement annotation of File records

## 0.13.6.6 (2013-05-29)

### Defect
* [SEQWARE-1644] - Multiple workflow output files and Pegasus
* [SEQWARE-1638] - Seems like setQueue not implemented

### Improvement
* [SEQWARE-1521] - Pipeline - We should add a Java API method that allows output files to specify their output path, this allows for overriding of output file locations.
* [SEQWARE-1648] - Specific order of chmod "commandlineArgs" required for OSX

### Story
* [SEQWARE-1479] - Repurpose our sqwdev.res environment so it's useful to SoftEng and PDE

### Task
* [SEQWARE-1457] - Distribution - Create 0.13.6.3 VM/AMI
* [SEQWARE-1573] - Verify STS logs 
* [SEQWARE-1609] - Key command line tools should have terminal mode similar to \x in postgres
* [SEQWARE-1634] - Basic Decider needs a function to check workflow run before launching

### Technical task
* [SEQWARE-1419] - Identify missing nanoc documentation
* [SEQWARE-1455] - Investigate the use of profiles so that we can do tests that require Pegasus on the VM
* [SEQWARE-1523] - The sample database has a ton of cruft in it, some of which doesn't render in portal. We should start with an empty DB on the VMs not the integration test DB.
* [SEQWARE-1529] - Missing functionality from the "User Tutorial"
* [SEQWARE-1533] - Control document creation via maven goal
* [SEQWARE-1581] - Produce Virtual Box VM (new) Don't worry about shrinking it
* [SEQWARE-1583] - Unify the directory structures on the AMI/VM
* [SEQWARE-1602] - On the way out of the AMI
* [SEQWARE-1611] - Assorted user-friendly features
* [SEQWARE-1620] - Create clean-up script

## 0.13.6.5 (2013-04-25)
<!-- includes a few tasks from 0.13.6.3 which was not released publically -->

### Defect
* [SEQWARE-1577] - AttributeAnnotator cascades deletes when annotating
* [SEQWARE-1595] - Race condition with .seqware/settings permissions "fix"

### Improvement
* [SEQWARE-1452] - Add 'workflows' column to mime types page


### Task
* [SEQWARE-1426] - Remove references to old seqware-pipeline.jar in our code
* [SEQWARE-1575] - Rebuild VM landing page
* [SEQWARE-1597] - Add table of overridden variables 
* [SEQWARE-1603] - Fix bugs that came up during tutorial with Morgan, Tim, and Vincent
* [SEQWARE-1608] - Java Workflows need the option to suppress creation of files for inputs to workflows
* [SEQWARE-1610] - Can't build on machines without .seqware/settings file
* [SEQWARE-1486] - ini-file value cannot be blank during template parsing 
* [SEQWARE-1491] - Workflow Launcher relaunches Java and Simplified FTL
* [SEQWARE-1500] - Setup dedicated SeqWare reference server

### Technical task
* [SEQWARE-1221] - Nonexistent Shared folder in VM
* [SEQWARE-1485] - Can't git pull the seqware directory on the VM
* [SEQWARE-1528] - User Tutorial Tasks
* [SEQWARE-1530] - Amazon-based User Tutorial
* [SEQWARE-1532] - SeqWare Portal docs (either paragraph or readme)
* [SEQWARE-1534] - Verify or clean Seqware Query Engine docs
* [SEQWARE-1535] - Simplify APIs page with only one link to most current Javadoc
* [SEQWARE-1536] - Spend one hour trying to merge web service docs into main site(Re-generate at least)
* [SEQWARE-1540] - The attribute annotator is unable to skip an IUS
* [SEQWARE-1553] - Pipeline - Generate Markdown for Modules (like it's done for plugins)
* [SEQWARE-1556] - Modules - Remove unused, workflow-specific modules to prevent user confusion
* [SEQWARE-1566] - Add MAVEN_OPTS to the .bash_profile
* [SEQWARE-1590] - Fix file provisioning for user tutorial
* [SEQWARE-1607] - Update install guide to indicate Amazon AMI settings
* [SEQWARE-1461] - Convert Perl integration test script into JUnit maven framework
* [SEQWARE-1496] - Create a test database for deciders and workflows

## 0.13.6.2 (2012-01-28)

### Defect
* [SEQWARE-1434] - Workflow_params and workflow_run_params are not populated when they are empty in the INI
* [SEQWARE-1444] - Some parameters from .ini cannot make it into MetaDB
* [SEQWARE-1471] - seqware installation error message confusing

### Task
* [SEQWARE-1453] - 0.13.6.1 Release
* [SEQWARE-1454] - Bam-QC Decider (Basic Decider) uses excessive amounts of memory
* [SEQWARE-1464] - Remove tomcat from sqwprod.hpc
* [SEQWARE-1465] - Install web-service to sqwweb.hpc
* [SEQWARE-1470] - Investigate (and fix) issue with BasicDecider on prod
* [SEQWARE-1473] - Add input (parent) file SWIDs to the StudyIDFilesTSVResource
* [SEQWARE-1474] - Workflow runs are not properly linked to IUS over the web service

## 0.13.6.1 (2013-01-09) 

### Improvement
* [SEQWARE-1297] - Make BasicDecider not re-attempt failed or processing workflows
* [SEQWARE-1409] - Make sure all plugins have descriptions

### Story
* [SEQWARE-1405] - Fix the newly identified BasicDecider bugs

### Task
* [SEQWARE-1284] - change setHeader in BasicDecider.java to something meaningful
* [SEQWARE-1410] - Null pointer when a directory has permission denied read access
* [SEQWARE-1411] - 0.13.6 Release 
* [SEQWARE-1425] - Create unit tests for existing decider functionality
* [SEQWARE-1427] - Create auto-generated doc for plugins
* [SEQWARE-1439] - mvn clean follows the symlinks
* [SEQWARE-1450] - Multi-threaded WorkflowStatusChecker
* [SEQWARE-1451] - New WorkflowLauncher does not launch subsequent workflows if one dies while launching

### Technical task
* [SEQWARE-1447] - Update docs/spec for multi-threaded WorkflowStatusChecker
* [SEQWARE-1448] - Create tests for multi-threaded WorkflowStatusChecker
* [SEQWARE-1449] - Create multi-threaded WorkflowStatusChecker

## 0.13.6 (2012-12-06)

### Defect
* [SEQWARE-1293] - Decider test incorrectly prints files that are not included
* [SEQWARE-1331] - Metadata allows you to add objects with incorrect accessions
* [SEQWARE-1333] - Metadata plugin does not write out files

### Improvement
* [SEQWARE-594] - use good separator in ini_file column 
* [SEQWARE-740] - Enhance the process/tool that generates the seqware meta database dump file such that all relationships (specifically duplicates) are tracked and reported
* [SEQWARE-1237] - add option to AttributeAnnotator for skip to propagate
* [SEQWARE-1298] - Request feature to give user last chance to cancel the workflow run (in modifyIniFile)
* [SEQWARE-1329] - ProcessingDataStructure2Dot improvements

### Story
* [SEQWARE-1218] - Modify the decider framework to run on everything, and on sequencer runs
* [SEQWARE-1288] - Make sure that ProvisionFiles has metadata writeback
* [SEQWARE-1325] - Web Service Access to Sample Hierarchy
* [SEQWARE-1341] - Fix the workflow archetype so that the metadata is correct

### Task
* [SEQWARE-1132] - GenericCommandRunner (and other modules) should have an option that allows you to check if a file was created without persisting it to the db
* [SEQWARE-1205] - Module Conventions needs to be somewhere writeable
* [SEQWARE-1371] - allow workflow user to set parentAccession in jobs and files
* [SEQWARE-1381] - seqware workflow java.lang.OutOfMemoryError
* [SEQWARE-1384] - bug fix for simplified workflow
* [SEQWARE-1394] - Trailing slash on bundle parameter causes null pointer exception
* [SEQWARE-1402] - Implementation of Improved WorkflowLauncher and Checker
* [SEQWARE-1404] - make a plugin to translate oozie workflow.xml to dot

### Technical task
* [SEQWARE-1219] - Create Decider archetype
* [SEQWARE-1355] - SequencerRun reports need to be fixed in SymLink Reporter
* [SEQWARE-1356] - Create WebService Resource for reporting
* [SEQWARE-1357] - Adding parameter for decider to get back report
* [SEQWARE-1365] - Java archetype puts incorrect package name (always net.sourceforge.seqware)
* [SEQWARE-1370] - Add ability to specify parent accessions for specific jobs
* [SEQWARE-1375] - add help message for workflowlauncherV2
* [SEQWARE-1378] - Make the BasicDecider run against everything in the DB
* [SEQWARE-1388] - Test Workflow monitor/launcher plugin with Morgan for SeqProdBio, ensure it works for multiple users
* [SEQWARE-1392] - fix bundle path for workflow
* [SEQWARE-1393] - bug fix for workflow directory name
* [SEQWARE-1399] - Simplify archetype parameter versions
* [SEQWARE-1403] - hostname and user file ownership utilities

## 0.13.5 (2012-11-13)

### Defect
* [SEQWARE-1224] - Genome Informatics talk and admin tutorial recovery
* [SEQWARE-1330] - Metadata add sequencer run error, possibly test db related
* [SEQWARE-1344] - NullPointer in webservice while marshalling XML

### Improvement
* [SEQWARE-1267] - Prepare TorBUG talk for 10/31
* [SEQWARE-1283] - non-informative error message, seqware crashes with NullPointerException

### Task
* [SEQWARE-1255] - Audit SeqWare resources
* [SEQWARE-1310] - implements oozie workflow engine
* [SEQWARE-1316] - Problem with ius (addIUS) command line api in seqware-full-0.13.4-SNAPSHOT.jar
* [SEQWARE-1318] - Work with tim to make sure sequencer_run, lane, and IUS writeback on the command line work.
* [SEQWARE-1327] - Setup and run tutorials on Pig for Pig-Fest
* [SEQWARE-1336] - prepare for AWG talk
* [SEQWARE-1338] - Write-up six month plan for Query Engine/SeqWare as a whole
* [SEQWARE-1339] - Create one-page high quality specifications

### Technical task
* [SEQWARE-863] - get reports/workflowruns error
* [SEQWARE-1097] - Add md5sum and size as Optional Fields to FileLinker Script
* [SEQWARE-1149] - Finish specification for RESTful web service
* [SEQWARE-1150] - Investigate web service options presented
* [SEQWARE-1261] - Resurrect BI for OHS
* [SEQWARE-1270] - throw exception if ini key is referenced but never setup
* [SEQWARE-1286] - Jetty should run when you call it explicitly
* [SEQWARE-1289] - add getStatus() getStdErr() getStdOut() in workflowEngine interface
* [SEQWARE-1308] - release simplified workflow for Morgan to try
* [SEQWARE-1311] - install oozie in VM
* [SEQWARE-1312] - write a HelloWorld oozie sample
* [SEQWARE-1313] - implements oozie workflow engine by extending the abstractworkflowengine
* [SEQWARE-1314] - read the oozie doc
* [SEQWARE-1315] - generate oozie xml from object model

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
* [SEQWARE-1276] - create a workflow client sample in VM that includes all features in the new simplified workflow
* [SEQWARE-1277] - discuss with Brian about the new workflow when it is ready
* [SEQWARE-1278] - change the workflow engine, allow it to take in parent workflowrun id for metadata writeback
* [SEQWARE-1281] - merge command line options with configs<string,string> in datamodel
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
* [SEQWARE-1182] - Configure Query Engine tests for Jenkins compatibility
* [SEQWARE-1187] - Test out JavaCC versus ANTLR
* [SEQWARE-1188] - Implement parse trees and RPNStack builder via chosen lexer/parser
* [SEQWARE-1189] - Adapt QueryVCFDumper  for query language
* [SEQWARE-1190] - Create plugin of FeatureFilters
* [SEQWARE-1194] - create a workflow template for the workflow author
* [SEQWARE-1195] - modify WorkflowLauncher to create seqwareWorkflowObjectModel
* [SEQWARE-1197] - implement vep sample using the latest java workflow
* [SEQWARE-1198] - finalize the spec for java workflow
* [SEQWARE-1199] - modify xml workflow based on the new java workflow
* [SEQWARE-1200] - move archetype-workflow-v2 to git feature branch
* [SEQWARE-1201] - add uncommitted code from svn to git
* [SEQWARE-1206] - Our workflow development docs on internal wiki need to be updated for 0.12.5.1
* [SEQWARE-1208] - Fix MetaDB creation
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
* SEQWARE-1142 - move sqwprod postgres data to NFS

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


