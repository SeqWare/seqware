---

title:                 "Plugins"
toc_includes_sections: true
markdown:              advanced

---

##  AttributeAnnotator
net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator

Experimental plugin. Allows the annotation of objects in the database with 'skip' values.

| Command-line option | Description |
|--------------------|--------------|
|--e, --experiment-accession|The SWID of the Experiment to annotate. One of the -accession options is required.|
|--file|The CSV file for bulk insert|
|--i, --ius-accession|The SWID of the IUS to annotate. One of the -accession options is required.|
|--key|Optional: The field that defines this attribute. The default value is 'skip'.|
|--l, --lane-accession|The SWID of the Lane to annotate. One of the -accession options is required.|
|--p, --processing-accession|The SWID of the Processing to annotate. One of the -accession options is required.|
|--s, --sample-accession|The SWID of the Sample to annotate. One of the -accession options is required.|
|--sequencer-run-accession, --sr|The SWID of the sequencer run to annotate. One of the -accession options is required.|
|--skip|Optional: Sets the 'skip' flag to either true or false for sequencer-run, lane, ius, or sample only.|
|--st, --study-accession|The SWID of the Study to annotate. One of the -accession options is required.|
|--value|Optional: The description of this field. If not specified, no attribute will be created.|
|--w, --workflow-accession|The SWID of the workflow to annotate. One of the -accession options is required.|
|--workflow-run-accession, --wr|The SWID of the workflow run to annotate. One of the -accession options is required.|

##  BasicDecider
net.sourceforge.seqware.pipeline.deciders.BasicDecider

The decider from which all other deciders came

| Command-line option | Description |
|--------------------|--------------|
|--all|Run everything. One of sample-name, study-name, sequencer-run-name or all is required.|
|--check-wf-accessions|The comma-separated, no spaces, workflow accessions of the workflow that perform the same function (e.g. older versions). Any files that have been processed with these workflows will be skipped.|
|--force-run-all|Forces the decider to run all matches regardless of whether they've been run before or not|
|--group-by|Optional: Group by one of the headings in FindAllTheFiles. Default: FILE_SWA. One of LANE_SWA or IUS_SWA.|
|--ho, --host|Used only in combination with --schedule to schedule onto a specific host. If not provided, the default is the local host|
|--ignore-skip-flag|Ignores any 'skip' flags on lanes, IUSes, sequencer runs, samples, etc. Use caution.|
|--launch-max|The maximum number of jobs to launch at once. Default: infinite.|
|--meta-types|The comma-separated meta-type(s) of the files to run this workflow with. Alternatively, use parent-wf-accessions.|
|--no-meta-db, --no-metadata|Optional: a flag that prevents metadata writeback (which is done by default) by the Decider and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.|
|--parent-wf-accessions|The workflow accessions of the parent workflows, comma-separated with no spaces. May also specify the meta-type.|
|--rerun-max|The maximum number of times to re-launch a workflowrun if failed. Default: 5.|
|--run|Run this workflow now. This is the default behaviour. See also: --schedule|
|--sample-name|Full sample name. One of sample-name, study-name, sequencer-run-name or all is required.|
|--schedule|Schedule this workflow to be run rather than running it immediately. See also: --run|
|--sequencer-run-name|Full sequencer run name. One of sample-name, study-name, sequencer-run-name or all is required.|
|--study-name|Full study name. One of sample-name, study-name, sequencer-run-name or all is required.|
|--test|Testing mode. Prints the INI files to standard out and does not submit the workflow.|
|--wf-accession|The workflow accession of the workflow|

##  BundleManager
net.sourceforge.seqware.pipeline.plugins.BundleManager

A plugin that lets you create, test, and install workflow bundles.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Optional: Provides this help message.|
|--b, --bundle|The path to a bundle zip file, can specify this or the workflow-run-accession of an already-installed bundle.|
|--download|Downloads a workflow bundle zip. This must be used in conjunction with a workflow name and version.|
|--download-url|Downloads a workflow bundle zip from a URL to the local directory.|
|--i, --install|Optional: if the --bundle param points to a .zip file then the install process will first unzip into the directory specified by the directory defined by SW_BUNDLE_DIR in the .seqware/settings file (skipping files that already exit).  It will then copy the whole zip file to the SW_BUNDLE_ARCHIVE_DIR which can be a directory or S3 prefix (the copy will be skipped if the file is already at this location). It will finish this process by installing this bundle in the database with the permanent_bundle_location pointed to the zip file location and current_working_dir pointed to the unzipped location.  If the --bundle param point to a directory then this will first create a zip of the bundle and place it in SW_BUNDLE_ARCHIVE_DIR. It will then install this bundle in the database with the permanent_bundle_location pointed to the zip file location and current_working_dir pointed to the unzipped location. The method (direct database or web service) and server location of the SeqWare  MetaDB is controlled via the .seqware/settings file.|
|--ido, --install-dir-only|Optional: This will suppress the creation of a zip file from a workflow bundle directory. It will simply install the workflow into the database and set the current_working_dir but leave permanent_bundle_location null.|
|--install-zip-only, --izo|Optional: This will suppress the unzipping of a zip file, it is only valid if the --bundle points to a zip file and not a directory. It will take a workflow bundle zip file, copy it to the SW_BUNDLE_ARCHIVE_DIR location, and then installs that workflow into the database.  Only the permanent_bundle_location location will be defined, the current_working_dir will be null. (PROBLEM: can't read the metadata.xml if the workflow zip isn't unzipped!)|
|--l, --list|Optional: List the workflows contained in this bundle.|
|--list-install, --list-installed|Optional: List the workflows contained in this bundle. The database/webservice must be enabled in your .seqware/settings for this option to work.|
|--list-params, --list-workflow-params|Optional: List the parameters for a given workflow and version. You need to supply a workflow accession and you need a database/webservice enabled in your .seqware/settings for this option to work.|
|--m, --metadata|Specify the path to the metadata.xml file.|
|--p, --path-to-package|Optional: When combined with a bundle zip file specified via --bundle this option specifies an input directory to zip to create a bundle output file.|
|--t, --test|Optional: This will trigger the test setup in the metadata.xml within this bundle.|
|--v, --validate|Optional: Run a light basic validation on this bundle.|
|--version, --workflow-version|The workflow version to be used. This must be used in conjunction with a version and bundle. Will restrict action to this workflow and version if specified.|
|--w, --workflow|The name of the workflow to be used. This must be used in conjunction with a version and bundle. Will restrict action to this workflow and version if specified.|
|--wa, --workflow-accession|Optional: The sw_accession of the workflow. Specify this or the workflow, version, and bundle. Currently used in conjunction with the list-workflow-params for now.|

##  CycleChecker
net.sourceforge.seqware.pipeline.plugins.CycleChecker

Checks for cycles in the sample hierarchy and processing hierarchy of a particular study and prints some information about the study

| Command-line option | Description |
|--------------------|--------------|
|--study-accession|The SeqWare accession of the study you want to check|

##  FileLinker
net.sourceforge.seqware.pipeline.plugins.FileLinker

Takes a list of files and enters them into the database, linking them with the appropriate IUSes and creating workflow runs for the 'FileImport' workflow. For more information, see https://sourceforge.net/apps/mediawiki/seqware/index.php?title=FileLinker

| Command-line option | Description |
|--------------------|--------------|
|--csv-separator||
|--file-list-file|A file containing the necessary information, with each line in the format parent_sw_accession, file path and mime-type. parent_sw_accession is either an IUS or a Lane.|
|--workflow-accession|The sw_accession of the Import files workflow|

##  HelloWorld
net.sourceforge.seqware.pipeline.plugins.HelloWorld

A very simple HelloWorld to show how to make plugins.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|

##  MarkdownPlugin
net.sourceforge.seqware.pipeline.plugins.MarkdownPlugin

A plugin that generates markdown documentation for all plugins.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--m, --modules|Optional: if provided will list out modules instead of plugins.|

##  Metadata
net.sourceforge.seqware.pipeline.plugins.Metadata

This plugin lets you list, read, and write to a collection of tables in the underlying MetaDB. This makes it easier to automate the creation of entities in the database which can be used as parents for file uploads and triggered workflows.

| Command-line option | Description |
|--------------------|--------------|
|--c, --create|Optional: indicates you want to create a new row, must supply --table and all the required --field params.|
|--f, --field|Optional: the field you are interested in writing. This is encoded as '<field_name>::<value>', you should use single quotes when the value includes spaces. You supply multiple --field arguments for a given table insert.|
|--interactive|Optional: Interactively prompt for fields during creation|
|--lf, --list-fields|Optional: if provided along with the --table option this will list out the fields for that table and their type.|
|--list-tables, --lt|Optional: if provided will list out the tables this tools knows how to read and/or write to.|
|--of, --output-file|Optional: if provided along with the --list or --list-tables options this will cause the output list of rows/tables to be written to the file specified rather than stdout.|
|--t, --table|Required: the table you are interested in reading or writing.|

##  ModuleRunner
net.sourceforge.seqware.pipeline.plugins.ModuleRunner

Description: A wrapper around Runner which will either list all Modules in the classpath (if no args are passed) or trigger a specific Module. Great for running Modules standalone.


##  OozieXML2Dot
net.sourceforge.seqware.pipeline.plugins.OozieXML2Dot

This take an input file of oozie workflow xml, and translate the relation of all actions into dot format

| Command-line option | Description |
|--------------------|--------------|
|--input||

##  ProcessingDataStructure2Dot
net.sourceforge.seqware.pipeline.plugins.ProcessingDataStructure2Dot

This plugin will take in a sw_accession of a processing, and translate the hierarchy of the processing relationship into dot format

| Command-line option | Description |
|--------------------|--------------|
|--output-file|Optional: file name|
|--parent-accession|The SWID of the processing|

##  SequencerRunReporter
net.sourceforge.seqware.pipeline.plugins.SequencerRunReporter

Prints a tab-delimited file describing the sequencer run, lane, sample, and algorithms run on every IUS in the database. For more information, see https://sourceforge.net/apps/mediawiki/seqware/index.php?title=Sequencer_Run_Reporter

| Command-line option | Description |
|--------------------|--------------|
|--output, --output-filename|Name of the output tab-delimited file.|

##  SymLinkFileReporter
net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter

Create a nested tree structure of all of the output files from a particular sample, or all of the samples in a study by using the SymLinkFileReporter plugin. This plugin also creates a CSV file with all of the accompanying information for every file. For more information, see https://sourceforge.net/apps/mediawiki/seqware/index.php?title=SymLink_Reporter

| Command-line option | Description |
|--------------------|--------------|
|--dump-all|Optional: Dumps all of the studies in the database to one file.|
|--duplicates|Optional: Allow duplications at the file path level|
|--f, --file-type|Optional: The file type to filter on. Only this type will be linked. Default is all files. Permissible file metatypes can found on the SeqWare Sourceforce Wiki under 'Module Conventions'|
|--l, --link|Optional: make hard links (P) or symlinks (s). Default is symlinks.|
|--no-links|Optional: Create only the CSV file, not the symlinked directories.|
|--output-filename|Optional: Name of the output CSV file (without the extension)|
|--prod-format|Optional: print the directories in prod format|
|--sample|Make symlinks for a sample|
|--sequencer-run|Make symlinks for a sequencerRun|
|--show-failed-and-running|Show all of the files regardless of the workflow run status. Default shows only successful runs.|
|--show-status|Show the workflow run status in the output CSV.|
|--study|Make symlinks for a study|
|--w, --workflow-accession|Optional: List all workflow runs with this workflow accession|

##  WorkflowLauncher
net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher

A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--b, --bundle, --provisioned-bundle-dir|The path to a bundle zip file. You can specify this or the workflow-accession of an already installed bundle.|
|--fh, --force-host|If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.|
|--ho, --host|Used only in combination with --schedule to schedule onto a specific host|
|--i, --ini-files|One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas without space to delimit a list of ini files.|
|--launch-scheduled, --ls|Optional: If this parameter is given (which can optionally have a comma separated list of workflow run accessions) all the workflows that have been scheduled in the database will have their commands constructed and executed on this machine (thus launching those workflows). This command can only be run on a machine capable of submitting workflows (e.g. a cluster submission host!). If you're submitting a workflow remotely you want to use the --schedule option instead.|
|--link-workflow-run-to-parents, --lwrp|Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).|
|--m, --metadata|Specify the path to the metadata.xml file.|
|--metadata-output-dir|Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.|
|--no-meta-db, --no-metadata|Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.|
|--pa, --parent-accessions|Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.|
|--s, --schedule|Optional: If this, the workflow-accession, and ini-files are all specified this will cause the workflow to be scheduled in the workflow run table rather than directly run. Useful if submitting the workflow to a remote server.|
|--status|Optional: Get the workflow status by ID|
|--v, --version, --workflow-version|The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.|
|--w, --workflow|The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.|
|--wa, --workflow-accession|Optional: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.|
|--wait|Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.|
|--workflow-engine|Optional: Specifies a workflow engine, we support Oozie and Pegasus. Default is Pegasus.|
|--workflow-run-accession, --wra|Optional: The sw_accession of an existing workflow_run that should be used. This row is pre-created when another job schedules a workflow run by partially populating a workflow_run row and setting the status to 'scheduled'. If this is not specified then a new workflow_run row will be created. Specify this in addition to a workflow-accession.|

##  WorkflowPlugin
net.sourceforge.seqware.pipeline.plugin.WorkflowPlugin

A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--b, --bundle, --provisioned-bundle-dir|The path to a bundle zip file. You can specify this or the workflow-accession of an already installed bundle.|
|--fh, --force-host|If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.|
|--ho, --host|Used only in combination with --schedule to schedule onto a specific host|
|--i, --ini-files|One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas without space to delimit a list of ini files.|
|--launch-scheduled, --ls|Optional: If this parameter is given (which can optionally have a comma separated list of workflow run accessions) all the workflows that have been scheduled in the database will have their commands constructed and executed on this machine (thus launching those workflows). This command can only be run on a machine capable of submitting workflows (e.g. a cluster submission host!). If you're submitting a workflow remotely you want to use the --schedule option instead.|
|--link-workflow-run-to-parents, --lwrp|Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).|
|--m, --metadata|Specify the path to the metadata.xml file.|
|--metadata-output-dir|Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.|
|--no-meta-db, --no-metadata|Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.|
|--pa, --parent-accessions|Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.|
|--s, --schedule|Optional: If this, the workflow-accession, and ini-files are all specified this will cause the workflow to be scheduled in the workflow run table rather than directly run. Useful if submitting the workflow to a remote server.|
|--status|Optional: Get the workflow status by ID|
|--v, --version, --workflow-version|The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.|
|--w, --workflow|The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.|
|--wa, --workflow-accession|Optional: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.|
|--wait|Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.|
|--workflow-engine|Optional: Specifies a workflow engine, we support Oozie and Pegasus. Default is Pegasus.|
|--workflow-run-accession, --wra|Optional: The sw_accession of an existing workflow_run that should be used. This row is pre-created when another job schedules a workflow run by partially populating a workflow_run row and setting the status to 'scheduled'. If this is not specified then a new workflow_run row will be created. Specify this in addition to a workflow-accession.|

##  WorkflowRunReporter
net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter

This plugin creates a tab-separated file that describes one or more workflow runs, including the identity, library samples and input and output files. For more information, see https://sourceforge.net/apps/mediawiki/seqware/index.php?title=Workflow_Run_Reporter

| Command-line option | Description |
|--------------------|--------------|
|--o, --output-filename|Optional: The output filename|
|--stdout|Prints to standard out instead of to a file|
|--t, --time-period|Dates to check for workflow runs. Dates are in format YYYY-MM-DD. If one date is provided, from that point to the present is checked. If two, separated by hyphen YYYY-MM-DDL:YYYY-MM-DD then it checks that range|
|--wa, --workflow-accession|The SWID of a workflow. All the workflow runs for that workflow will be retrieved.|
|--workflow-run-accession, --wra|The SWID of the workflow run|
|--wr-stderr|Optional: will print the stderr of the workflow run, must specify the --workflow-run-accession|
|--wr-stdout|Optional: will print the stdout of the workflow run, must specify the --workflow-run-accession|

##  WorkflowStatusChecker
net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker

This plugin lets you monitor the status of running workflows and updatesthe metadata object with their status.  Keep in mind a few things: 1) if the status command is specified no data will be saved to the DB, this tool is just useful for gathering error reports, 2) status commands that are malformed or whose status directory is not present on the filesystem will be skipped and an error noted, 3) by default every running or unknown workflow_run in the database will be checked if they are owned by the username in your .seqware/settings file and the hostname is the same as 'hostname --long', and 4) you can force the checking of workflows with a particular host value but be careful with that.

| Command-line option | Description |
|--------------------|--------------|
|--cf, --check-failed|Optional: if specified, workflow runs that have previously failed will be re-checked.|
|--check-unknown, --cu|Optional: if specified, workflow runs that have previously marked unknown will be re-checked.|
|--fh, --force-host|Optional: if specified, workflow runs scheduled to this specified host will be checked even if this is not the current host (a dangerous option).|
|--s, --status-cmd|Optional: the Pegasus status command, if you specify this option the command will be run, potentially displaying the summarized/parsed errors, but the database will not be updated.|
|--threads-in-thread-pool, --tp|Optional: this will determine the number of threads to run with. Default: 1|
|--wa, --workflow-accession|Optional: this will cause the program to only check the status of workflow runs that are this type of workflow.|
|--workflow-run-accession, --wra|Optional: this will cause the program to only check the status of this particular workflow run.|
