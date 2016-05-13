---

title:                 "Plugins"
toc_includes_sections: true
markdown:              advanced

---


##  AttributeAnnotator
net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator
Allows the annotation of objects in the database with 'skip' values.

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--e, --experiment-accession|The SWID of the Experiment to annotate. One of the -accession options is required.|
|--f, --file-accession|The SWID of the file to annotate. One of the -accession options is required.|
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
|--[arguments]|Override ini options on the command after the separator "--" with pairs of "--<key> <value>"|
|--all|Operate across everything. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. |
|--cf, --check-file-exists|Optional: only launch on the file if the file exists|
|--check-wf-accessions|The comma-separated, no spaces, workflow accessions of the workflow that perform the same function (e.g. older versions). Any files that have been processed with these workflows will be skipped.|
|--force-run-all|Forces the decider to run all matches regardless of whether they've been run before or not|
|--group-by|Optional: Group by one of the headings in FindAllTheFiles. Default: FILE_SWA. One of LANE_SWA or IUS_SWA.|
|--ho, --host|Used only in combination with --schedule to schedule onto a specific host. If not provided, the default is the local host|
|--ignore-skip-flag|Ignores any 'skip' flags on lanes, IUSes, sequencer runs, samples, etc. Use caution.|
|--ius-SWID|ius-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --ius-SWID|
|--lane-SWID|lane-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --lane-SWID|
|--launch-max|The maximum number of jobs to launch at once.|
|--meta-types|The comma-separated meta-type(s) of the files to run this workflow with. Alternatively, use parent-wf-accessions.|
|--no-meta-db, --no-metadata|Optional: a flag that prevents metadata writeback (which is done by default) by the Decider and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.|
|--organism|organism. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --organism|
|--parent-wf-accessions|The workflow accessions of the parent workflows, comma-separated with no spaces. May also specify the meta-type.|
|--processing-SWID|processing-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --processing-SWID|
|--processing-status|processing-status. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --processing-status|
|--rerun-max|The maximum number of times to re-launch a workflowrun if failed.|
|--root-sample-name|root-sample-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --root-sample-name|
|--sample-name|sample-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --sample-name|
|--sequencer-run-name|sequencer-run-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --sequencer-run-name|
|--study-name|study-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --study-name|
|--test|Testing mode. Prints the INI files to standard out and does not submit the workflow.|
|--wf-accession|The workflow accession of the workflow|
|--workflow-run-status|workflow-run-status. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --workflow-run-status|


##  BatchMetadataInjection
net.sourceforge.seqware.pipeline.plugins.BatchMetadataInjection
Import objects into the database using different file formats.

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--c, --create|Optional: indicates you want to create a new row, must supply --table and all the required --field params.|
|--export-json-sequencer-run|The location to export the RunInfo json output file.|
|--f, --field|Optional: the field you are interested in writing. This is encoded as '<field_name>::<value>', you should use single quotes when the value includes spaces. You supply multiple --field arguments for a given table insert.|
|--file|Optional: one file option can be specified when you create a file, one or more --file options can be specified when you create a workflow_run. This is encoded as '<algorithm>::<file-meta-type>::<file-path>', you should use single quotes when the value includes spaces.|
|--import-json-sequencer-run|The location of the RunInfo json file to import.|
|--input-file|Optional: one or more --input-file options can be specified when you create a workflow_run. This is encoded as a SWID|
|--interactive|Optional: turn on interactive input |
|--lf, --list-fields|Optional: if provided along with the --table option this will list out the fields for that table and their type.|
|--list-tables, --lt|Optional: if provided will list out the tables this tools knows how to read and/or write to.|
|--miseq-sample-sheet|The location of the Miseq Sample Sheet|
|--new|Create a new study from scratch. Used instead of miseq-sample-sheet|
|--of, --output-file|Optional: if provided along with the --list or --list-tables options this will cause the output list of rows/tables to be written to the file specified rather than stdout.|
|--parent-accession|Optional: one or more --parent-accession options can be specified when you create a workflow_run.|
|--record|Optional: saves information about the injection in a text file|
|--t, --table|Required: the table you are interested in reading or writing.|
|--validate-json-sequencer-run|The location of the RunInfo json file to validate.|


##  BundleManager
net.sourceforge.seqware.pipeline.plugins.BundleManager
A plugin that lets you create, test, and install workflow bundles.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Optional: Provides this help message.|
|--[arguments]||
|--b, --bundle|The path to a bundle zip file, can specify this or the workflow-run-accession of an already-installed bundle.|
|--download|Downloads a workflow bundle zip. This must be used in conjunction with a workflow name and version.|
|--download-url|Downloads a workflow bundle zip from a URL to the local directory.|
|--ha, --human-aligned|Optional: will print output in aligned human friendly format|
|--he, --human-expanded|Optional: will print output in expanded human friendly format|
|--i, --install|Optional: if the --bundle param points to a .zip file then the install process will first unzip into the directory specified by the directory defined by SW_BUNDLE_DIR in the .seqware/settings file (skipping files that already exit).  It will then copy the whole zip file to the SW_BUNDLE_REPO_DIR which can be a directory or S3 prefix (the copy will be skipped if the file is already at this location). It will finish this process by installing this bundle in the database with the permanent_bundle_location pointed to the zip file location and current_working_dir pointed to the unzipped location.  If the --bundle param point to a directory then this will first create a zip of the bundle and place it in SW_BUNDLE_REPO_DIR. It will then install this bundle in the database with the permanent_bundle_location pointed to the zip file location and current_working_dir pointed to the unzipped location. The method (direct database or web service) and server location of the SeqWare  MetaDB is controlled via the .seqware/settings file.|
|--ido, --install-dir-only|Optional: This will suppress the creation of a zip file from a workflow bundle directory. It will simply install the workflow into the database and set the current_working_dir but leave permanent_bundle_location null.|
|--install-zip-only, --izo|Optional: This will suppress the unzipping of a zip file, it is only valid if the --bundle points to a zip file and not a directory. It will take a workflow bundle zip file, copy it to the SW_BUNDLE_REPO_DIR location, and then installs that workflow into the database.  Only the permanent_bundle_location location will be defined, the current_working_dir will be null. (PROBLEM: can't read the metadata.xml if the workflow zip isn't unzipped!)|
|--l, --list|Optional: List the workflows contained in this bundle.|
|--list-install, --list-installed|Optional: List the workflows contained in this bundle. The database/webservice must be enabled in your .seqware/settings for this option to work.|
|--list-params, --list-workflow-params|Optional: List the parameters for a given workflow and version. You need to supply a workflow accession and you need a database/webservice enabled in your .seqware/settings for this option to work.|
|--m, --metadata|Specify the path to the metadata.xml file.|
|--out|Optional: Will output a list of workflows installed by sw_accession|
|--p, --path-to-package|Optional: When combined with a bundle zip file specified via --bundle this option specifies an input directory to zip to create a bundle output file.|
|--v, --validate|Optional: Run a light basic validation on this bundle.|
|--version, --workflow-version|The workflow version to be used. This must be used in conjunction with a version and bundle. Will restrict action to this workflow and version if specified.|
|--w, --workflow|The name of the workflow to be used. This must be used in conjunction with a version and bundle. Will restrict action to this workflow and version if specified.|
|--wa, --workflow-accession|Optional: The sw_accession of the workflow. Specify this or the workflow, version, and bundle. Currently used in conjunction with the list-workflow-params for now.|


##  CheckDB
net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB
A database validation tool for your SeqWare metadb.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||


##  CycleChecker
net.sourceforge.seqware.pipeline.plugins.CycleChecker
Checks for cycles in the sample hierarchy and processing hierarchy of a particular study and prints some information about the study

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--study-accession|The SeqWare accession of the study you want to check|


##  DeletionDB
net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB
A database deletion tool for your SeqWare metadb.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--k, --key|An existing key file will be used to guide an actual deletion process|
|--o, --out|The filename of the output key file|
|--r, --workflowrun|Give a sequencer run, lane, or workflow run SWID in order to determine which workflow runs (processings,files) should be deleted.|


##  FileLinker
net.sourceforge.seqware.pipeline.plugins.FileLinker
Takes a list of files and enters them into the database, linking them with the appropriate IUSes and creating workflow runs for the 'FileImport' workflow. For more information, see http://seqware.github.com/docs/22-filelinker/

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--file-list-file|A file containing the necessary information, with each line in the format parent_sw_accession, file path and mime-type. parent_sw_accession is either an IUS or a Lane.|
|--workflow-accession|The sw_accession of the Import files workflow|


##  FileProvenanceQueryTool
io.seqware.pipeline.plugins.FileProvenanceQueryTool
Pulls back a file provenance report (or a previous tab-separated file), runs an arbitrarily complex SQL query on the results and saves the results as a tab separated file for use as a part of interpreted language deciders.

| Command-line option | Description |
|--------------------|--------------|
|--H2mem|Use H2 in-memory database for better performance (but with memory limits)|
|--[arguments]||
|--all|Operate across everything. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. |
|--in|The tab separated file that will be used instead of pulling back a fresh file provenance report. Must be a tab separated file with a fixed number of columns with a provided header (that will be used for column names). |
|--ius-SWID|ius-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --ius-SWID|
|--lane-SWID|lane-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --lane-SWID|
|--organism|organism. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --organism|
|--out|The tab separated file into which the results will be written.|
|--processing-SWID|processing-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --processing-SWID|
|--processing-status|processing-status. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --processing-status|
|--query|The standard SQL query that should be run. Table queried should be FILE_REPORT|
|--root-sample-name|root-sample-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --root-sample-name|
|--sample-name|sample-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --sample-name|
|--sequencer-run-name|sequencer-run-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --sequencer-run-name|
|--study-name|study-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --study-name|
|--workflow-run-status|workflow-run-status. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --workflow-run-status|


##  FileProvenanceReporter
net.sourceforge.seqware.pipeline.plugins.fileprovenance.FileProvenanceReporter
Generates a tab-delimited report of all output files (and their relationships and metadata) from a specified study or from all studies.

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--all|Operate across everything. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. |
|--ius-SWID|ius-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --ius-SWID|
|--lane-SWID|lane-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --lane-SWID|
|--organism|organism. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --organism|
|--out|The file into which the report will be written.|
|--processing-SWID|processing-SWID. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --processing-SWID|
|--processing-status|processing-status. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --processing-status|
|--root-sample-name|root-sample-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --root-sample-name|
|--sample-name|sample-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --sample-name|
|--sequencer-run-name|sequencer-run-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --sequencer-run-name|
|--study-name|study-name. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --study-name|
|--workflow-run-status|workflow-run-status. At least one of [lane-SWID, ius-SWID, study-name, sample-name, root-sample-name, sequencer-run-name, organism, workflow-run-status, processing-status, processing-SWID]  or all is required. Specify multiple names by repeating --workflow-run-status|


##  HelloWorld
net.sourceforge.seqware.pipeline.plugins.HelloWorld
A very simple HelloWorld to show how to make plugins.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||


##  MarkdownPlugin
net.sourceforge.seqware.pipeline.plugins.MarkdownPlugin
A plugin that generates markdown documentation for all plugins.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--m, --modules|Optional: if provided will list out modules instead of plugins.|
|--s, --skip|Optional: comma separated list of module/plugin names to skip|


##  Metadata
net.sourceforge.seqware.pipeline.plugins.Metadata
This plugin lets you list, read, and write to a collection of tables in the underlying MetaDB. This makes it easier to automate the creation of entities in the database which can be used as parents for file uploads and launched workflows.

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--c, --create|Optional: indicates you want to create a new row, must supply --table and all the required --field params.|
|--f, --field|Optional: the field you are interested in writing. This is encoded as '<field_name>::<value>', you should use single quotes when the value includes spaces. You supply multiple --field arguments for a given table insert.|
|--file|Optional: one file option can be specified when you create a file, one or more --file options can be specified when you create a workflow_run. This is encoded as '<algorithm>::<file-meta-type>::<file-path>', you should use single quotes when the value includes spaces.|
|--input-file|Optional: one or more --input-file options can be specified when you create a workflow_run. This is encoded as a SWID|
|--interactive|Optional: Interactively prompt for fields during creation|
|--lf, --list-fields|Optional: if provided along with the --table option this will list out the fields for that table and their type.|
|--list-tables, --lt|Optional: if provided will list out the tables this tools knows how to read and/or write to.|
|--of, --output-file|Optional: if provided along with the --list or --list-tables options this will cause the output list of rows/tables to be written to the file specified rather than stdout.|
|--parent-accession|Optional: one or more --parent-accession options can be specified when you create a workflow_run.|
|--t, --table|Required: the table you are interested in reading or writing.|


##  ModuleRunner
net.sourceforge.seqware.pipeline.plugins.ModuleRunner
Description: A wrapper around Runner which will either list all Modules in the classpath (if no args are passed) or trigger a specific Module. Great for running Modules standalone.

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||


##  OozieXML2Dot
net.sourceforge.seqware.pipeline.plugins.OozieXML2Dot
This take an input file of oozie workflow xml, and translate the relation of all actions into dot format

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||


##  ProcessingDataStructure2Dot
net.sourceforge.seqware.pipeline.plugins.ProcessingDataStructure2Dot
This plugin will take in a sw_accession of a processing, and translate the hierarchy of the processing relationship into dot format

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--output-file|Optional: file name|
|--parent-accession|The SWID of the processing|


##  UserSettingsPlugin
io.seqware.pipeline.plugins.UserSettingsPlugin
A plugin that generates a commented .seqware settings file that can be used for documentation.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||


##  WorkflowLauncher
io.seqware.pipeline.plugins.WorkflowLauncher
A plugin that lets you launch scheduled workflow bundles.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--fh, --force-host|If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.|
|--launch-scheduled, --ls|Optional: If this parameter is given (which can optionally have a comma separated list of workflow run accessions) all the workflows that have been scheduled in the database will have their commands constructed and executed on this machine (thus launching those workflows). This command can only be run on a machine capable of submitting workflows (e.g. a cluster submission host!).|
|--no-run|Optional: Terminates the launch process immediately prior to running. Useful for debugging.|


##  WorkflowLifecycle
io.seqware.pipeline.plugins.WorkflowLifecycle
A plugin that lets you (install)/schedule/launch/watch/status check workflows in one fell swoop

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]|Override ini options on the command after the separator "--" with pairs of "--<key> <value>"|
|--b, --bundle, --provisioned-bundle-dir|The path to an unzipped bundle. Specify a name and version as well if the bundle contains multiple workflows.|
|--i, --ini-files|One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas to delimit a list of ini files.|
|--no-meta-db, --no-metadata|Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.|
|--no-run|Optional: Terminates the launch process immediately prior to running. Useful for debugging.|
|--pa, --parent-accessions|Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.|
|--v, --version, --workflow-version|The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.|
|--w, --workflow|The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.|
|--wait|Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.|
|--workflow-accession|The accession for an installed workflow, must be provided unless a bundle is.|
|--workflow-engine|Optional: Specifies a workflow engine, one of: oozie, oozie-sge, whitestar, whitestar-parallel, whitestar-sge. Defaults to oozie.|


##  WorkflowRelauncher
io.seqware.pipeline.plugins.WorkflowRelauncher
A plugin that lets you re-launch failed workflow runs.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--w, --working-dir|Required: The working directory of the workflow run to watch|


##  WorkflowRescheduler
io.seqware.pipeline.plugins.WorkflowRescheduler
A plugin that lets you re-schedule previously launched workflow runs.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--ho, --host|Used to schedule onto a specific host|
|--out|Optional: Will output a workflow-run by sw_accession|
|--workflow-engine|Optional: Specifies a workflow engine, one of: oozie, oozie-sge, whitestar, whitestar-parallel, whitestar-sge. Defaults to oozie.|
|--workflow-run, --wr|Required: specify workflow-run(s) by swid, comma-delimited, to re-schedule|


##  WorkflowRunFilesInitialPopulationPlugin
net.sourceforge.seqware.pipeline.plugins.WorkflowRunFilesInitialPopulationPlugin
This plugin does the initial population of workflow run files in order to track input files.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--m, --modules|Optional: if provided will list out modules instead of plugins.|
|--s, --skip|Optional: comma separated list of module/plugin names to skip|


##  WorkflowRunReporter
net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter
This plugin creates a tab-separated file that describes one or more workflow runs, including the identity, library samples and input and output files. For more information, see see http://seqware.github.com/docs/19-workflow-run-reporter/

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--human|Optional: will print output in expanded human friendly format|
|--o, --output-filename|Optional: The output filename|
|--status|Optional: Specify a particular status to restrict workflow runs that will be returned, status is one of [submitted, pending, running, failed, completed, submitted_cancel, cancelled, submitted_retry]|
|--stdout|Prints to standard out instead of to a file|
|--t, --time-period|Dates to check for workflow runs. Dates are in format YYYY-MM-DD. If one date is provided, from that point to the present is checked. If two, separated by hyphen YYYY-MM-DDL:YYYY-MM-DD then it checks that range|
|--wa, --workflow-accession|The SWID of a workflow. All the workflow runs for that workflow will be retrieved.|
|--workflow-run-accession, --wra|The SWID of the workflow run|
|--wr-stderr|Optional: will print the stderr of the workflow run, must specify the --workflow-run-accession|
|--wr-stdout|Optional: will print the stdout of the workflow run, must specify the --workflow-run-accession|


##  WorkflowScheduler
io.seqware.pipeline.plugins.WorkflowScheduler
A plugin that lets you schedule installed workflow bundles.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]|Override ini options on the command after the separator "--" with pairs of "--<key> <value>"|
|--ho, --host|Used to schedule onto a specific host|
|--i, --ini-files|One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas to delimit a list of ini files.|
|--if, --input-files|One or more input files can be specified as sw_accessions for metadata tracking of input files. Use commas to delimit a list of input files.|
|--link-workflow-run-to-parents, --lwrp|Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).|
|--no-meta-db, --no-metadata|Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.|
|--out|Optional: Will output a workflow-run by sw_accession|
|--pa, --parent-accessions|Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.|
|--wa, --workflow-accession|Required: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.|
|--workflow-engine|Optional: Specifies a workflow engine, one of: oozie, oozie-sge, whitestar, whitestar-parallel, whitestar-sge. Defaults to oozie.|


##  WorkflowStatusChecker
net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker
This plugin lets you monitor the status of running workflows and updates the metadata object with their status. By default every running or unknown workflow_run in the database will be checked if they are owned by the username in your .seqware/settings file and the hostname is the same as 'hostname --long'. You can force the checking of workflows with a particular host value but be careful with that.

| Command-line option | Description |
|--------------------|--------------|
|--[arguments]||
|--cf, --check-failed|Optional: if specified, workflow runs that have previously failed will be re-checked.|
|--fh, --force-host|Optional: if specified, workflow runs scheduled to this specified host will be checked even if this is not the current host (a dangerous option).|
|--threads-in-thread-pool, --tp|Optional: this will determine the number of threads to run with. Default: 1|
|--wa, --workflow-accession|Optional: this will cause the program to only check the status of workflow runs that are this type of workflow.|
|--workflow-run-accession, --wra|Optional: this will cause the program to only check the status of workflow run(s). For multiple runs, comma-separate with no spaces|


##  WorkflowWatcher
io.seqware.pipeline.plugins.WorkflowWatcher
A plugin that lets you watch running workflow_runs.

| Command-line option | Description |
|--------------------|--------------|
|--?, --h, --help|Provides this help message.|
|--[arguments]||
|--r, --workflow-run-accession, --wra|Required: The sw_accession of the workflow run to watch|
