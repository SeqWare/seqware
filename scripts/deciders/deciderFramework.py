#! /usr/bin/env python3
import subprocess, sys, argparse, importlib, tempfile, configparser, itertools, csv, collections, socket
from builtins import int
from configparser import ConfigParser
from os.path import expanduser
from tempfile import TemporaryDirectory
from argparse import PARSER

###################################################################
## This is an implementation of a 
## decider framework. This class is not designed for inheritance.
###################################################################

supportedConstraints = ['study-name', 'lane-SWID', 'ius-SWID', 'sample-name', 'root-sample-name', 'sequencer-run-name', 'organism', 'processing-SWID']

def _main():

    # figure out version of seqware on the path
    output = subprocess.check_output(["seqware","--version"], universal_newlines=True)
    seqwareVersion = output.rpartition(' ')[2].strip()

    parser = argparse.ArgumentParser(description='Run a SeqWare decider in order to schedule workflow runs')
    targetGroup = parser.add_argument_group('constraints','these parameters constrain what fraction of the files report your decider will be running against')
    targetGroup.add_argument('--all', help='Target the decider at all data in your SeqWare provenance report', action='store_true')
    for constraint in supportedConstraints:
        targetGroup.add_argument('--'+ constraint, help='Target the decider at a ' + constraint + ', can be specified multiple times', action='append')
    parser.add_argument('--test', help='Output commands for scheduling potential workflow runs instead of actually scheduling them', action='store_true')
    parser.add_argument('--host', help='Schedule onto a particular host', default=socket.gethostname())
    parser.add_argument('--launch-max', help='The maximum number of jobs to launch at once', default=sys.maxsize, type=int)
    parser.add_argument('--meta-types', help='The meta-type(s) of files to run a workflow with', action='append')
    parser.add_argument('--deciderImpl', help='The name of a Python class that contains the implementation of the decider', default='basicDeciderImpl')
    parser.add_argument('wfAccession', help='The sw_accession of the workflow that we wish to run', type=int)
    parser.add_argument('--extraHelp', help='Display extended help for workflow options when wfAccession is specified', action='store_true')
    
    # parse just known arguments and ignore arguments that may be for the workflow
    args = parser.parse_known_args()[0]   
    print("Initial Arguments")     
    print(args)
    _validateArguments(args, supportedConstraints)
    ## for debugging print the arguments
    
    # try to dynamically create new options based on a particular workflow
    temporaryDirectory = tempfile.TemporaryDirectory()
    workflowAccession = args.wfAccession
    output = subprocess.check_output(["seqware","workflow","ini","--accession",str(workflowAccession),"--out",temporaryDirectory.name + "/workflow.ini"], universal_newlines=True)
    config = ConfigParser()
    # append an artificial section header to match python config files
    config.read_file(itertools.chain(['[DEFAULT_SECTION]'], open(temporaryDirectory.name + "/workflow.ini")))
    
    # this section dynamically loads the desired decider implementation class
    decider = importlib.import_module(args.deciderImpl)
    Decider = getattr(decider, 'Decider')
    deciderImpl = Decider()
    workflowGroup = parser.add_argument_group('workflow parameters', 'these workflow parameters can be overridden and have behaviour dependent on the workflow')
    
    # automatically load ini file parameters and list them under usage for overriding
    for key in config['DEFAULT_SECTION']: 
        workflowGroup.add_argument('--' + key)

    # NOTE: this is the first hook for custom deciders. Authors can customize the parser object with new parameters
    deciderImpl.init(parser)
    
    if args.extraHelp:
        parser.print_help()
        sys.exit()
    args = parser.parse_args()
    
    # print out arguments for second pass
    print("Complete Arguments:")
    print(args)
    reportFile = _queryWSForProvenanceReport(args, temporaryDirectory, deciderImpl, seqwareVersion)
    
    # read the provenance report and create a data structure for workflow developers
    # file report will be a multi-map with group_col -> column from files report -> value
    filesReport = collections.defaultdict(list)
    with open(reportFile) as csvfile:
        csvfile_reader = csv.DictReader(csvfile, delimiter='\t')
        for row in csvfile_reader:
            # if there is a metatype filter in place, drop out rows that don't match 
            if (args.meta_types is not None and row["file_meta_type"] not in args.meta_types):
                print("Skipping since " + row["file_meta_type"] + " not in " + str(args.meta_types))
                continue
            checkFileDetails = deciderImpl.checkFileDetails(row)
            if not checkFileDetails:
                continue
            filesReport[row["group_col"]].append(row)

    # main action of the decider, here we go through each group and examine it for possible launching
    launched = 0
    for groupKey, groupVal in filesReport.items():
        
        print("Examining group: " + groupKey + " with length " + str(len(groupVal)))
        for dict in groupVal:
            print("\t " + dict["file_path"])
        # this is where we would hook in --ignore-previous-runs
        rerun = _checkPreviousRuns(groupVal)
        finalCheck = deciderImpl.doFinalCheck(groupVal)
        defaultIni = config['DEFAULT_SECTION']
        
        if finalCheck:
            if rerun:
                workflowIni = _modifyIniFile(defaultIni, args, groupVal, deciderImpl)
                launched += 1
                command = _constructCommand(workflowIni, groupVal, args.host, seqwareVersion, workflowAccession)
                commandStr = ' '.join(str(x) for x in command)
            
                if args.test:
                    print("Test Mode - would have scheduled the following:")
                    print(commandStr)
                else:
                    print("Live Mode - Scheduling:")
                    print(commandStr)
                    output = subprocess.check_output( command , universal_newlines=True)
                    print(output)
            else:
                if args.test:
                    print("Test Mode - rerun false, skipping group " + groupKey)
                else:
                    print("Live Mode - rerun false, skipping group " + groupKey)
        else:
            print("Final check failed, aborting run.")
        # abort if scheduled the maximum number of workflow-runs
        if launched >= args.launch_max:
            print("Reached maximum number of launches " + str(args.launch_max))
            sys.exit()

''' construct the actual schedule command as an array '''
def _constructCommand(iniFile, workflowRunGroup, host, seqwareVersion, workflowAccession):
    # construct the java -jar command-line
    runArgs = ["java","-jar",expanduser("~") + "/.seqware/self-installs/seqware-distribution-"+seqwareVersion+"-full.jar"]
    runArgs.append("--plugin")
    runArgs.append("io.seqware.pipeline.plugins.WorkflowScheduler")
    runArgs.append("--")
    runArgs.append("--workflow-accession")
    runArgs.append(str(workflowAccession))
    runArgs.append("--ini-files")
    runArgs.append(iniFile)
    
    runArgs.append("--input-files")
    fileArgs = []
    for dict in workflowRunGroup:
        fileArgs.append(dict["file_swid"])
    runArgs.append(','.join(fileArgs))
        
    # these next two behaviours should be deprecated in favour of simply tracking input files
    # mimic the Java BasicDecider and hook up processing_swid 
    processingArgs = []
    runArgs.append("--parent-accessions")
    for dict in workflowRunGroup:
        processingArgs.append(dict["processing_swid"])
    runArgs.append(','.join(processingArgs))
    # mimic the Java BasicDecider and hook up ius_swa or if not present link up the lane_swa
    laneOrIusArgs = []
    runArgs.append("--link-workflow-run-to-parents")
    for dict in workflowRunGroup:
        val = dict["ius_swid"]
        if (len(val.strip()) == 0):
            val = dict["lane_swid"]
        laneOrIusArgs.append(val)
    runArgs.append(','.join(laneOrIusArgs))

    runArgs.append("--host")
    runArgs.append(host)
    
    return runArgs
        
''' queries the WS via CLI in order to determine where there are any blocking workflow runs '''
def _checkPreviousRuns(dict):
    # check previous workflow runs 
    # special case, when rerun max is 0, we still want to launch even if there are 0 failures
    # check against rerunMax when hook is needed
    # processWorkflowRuns
    # implement logic table for both with one workflow specified or when multiple workflows are specified
    return True

''' allow decider implementation to modify ini file '''
def _modifyIniFile(dict, args, workflowGroup, deciderImpl):
    # merge parser arguments with config
    for key, value in dict.items():
        try:
            newValue = getattr(args, key.replace('-','_'))
            if newValue is not None:
                print("Replacing {0}={1} with {2}={3}".format(key,value,key,newValue))
                dict[key] = newValue 
        except AttributeError:
            pass
    # allow decider implementation to do further modifications based on group data
    dict = deciderImpl.modifyIniFile(dict, workflowGroup)
    
    # create a basic ini file based on the combination of the ini file pulled from the web service and argument parameters
    iniFile = tempfile.NamedTemporaryFile(prefix='workflow', suffix='.ini', delete=False)
    print("Created " + iniFile.name)
    for key, value in dict.items():
        line = "{0}={1}\n".format(key,value)
        iniFile.write(line.encode('utf-8')) 
    iniFile.close()
    return iniFile.name

        
''' queries the file provenance report '''   
def _queryWSForProvenanceReport(args, temporaryDirectory, deciderImpl, seqwareVersion):
    returnFile = temporaryDirectory.name + "/output.tsv"
    # invoke the provenance report query tool with a query provided by the decider 
    query = deciderImpl.provideTargetQuery()
    commandArray = ["java","-jar",expanduser("~") + "/.seqware/self-installs/seqware-distribution-"+seqwareVersion+"-full.jar","-p","io.seqware.pipeline.plugins.FileProvenanceQueryTool","--", "--out", returnFile, "--query", query]
    if args.all:
        commandArray.append("--all")
    else:
        for constraint in supportedConstraints:
            try:
                value = getattr(args, constraint.replace('-','_'))
                if value is not None:
                    for listValue in value:
                        commandArray.append("--"+constraint)
                        commandArray.append(listValue) 
            except AttributeError:
                pass
    output = subprocess.check_output( commandArray , universal_newlines=True)
    return returnFile
    
'''Validates arguments passed on the command line to make sure that they are reasonable'''
def _validateArguments(args, supportedConstraints):
    specificConstraintDetected = False
    for constraint in supportedConstraints:
            try:
                value = getattr(args, constraint.replace('-','_'))
                if value is not None:
                    specificConstraintDetected = True
            except AttributeError:
                pass
    
    if args.all and specificConstraintDetected:
        print( "Cannot specify --all in combination with " + constraint )
        sys.exit(-1)
    if not args.all and not specificConstraintDetected: 
        print( "Need to specify --all or some other constraint" )
        sys.exit(-1)
        
_main()
