#! /usr/bin/env python3
import subprocess, sys, argparse, importlib, tempfile, configparser, itertools, csv, collections
from builtins import int
from configparser import ConfigParser
from os.path import expanduser
from tempfile import TemporaryDirectory
from argparse import PARSER

###################################################################
## This is an implementation of a 
## decider framework. This class is not designed for inheritance.
###################################################################

def _main():

    parser = argparse.ArgumentParser(description='Run a SeqWare decider in order to schedule workflow runs')
    targetGroup = parser.add_argument_group('constraints','these parameters constrain what fraction of the files report your decider will be running against')
    targetGroup.add_argument('--all', help='Target the decider at all data in your SeqWare provenance report', action='store_true')
    supportedConstraints = ['study-name', 'lane-SWID', 'ius-SWID', 'sample-name', 'root-sample-name', 'sequencer-run-name', 'organism', 'processing-SWID']
    for constraint in supportedConstraints:
        targetGroup.add_argument('--'+ constraint, help='Target the decider at a ' + constraint + ', can be specified multiple times', action='append')
    parser.add_argument('--test', help='Output commands for scheduling potential workflow runs instead of actually scheduling them', action='store_true')
    parser.add_argument('--host', help='Schedule onto a particular host')
    parser.add_argument('--launch-max', help='The maximum number of jobs to launch at once', default=sys.maxsize, type=int)
    parser.add_argument('--meta-types', help='The meta-type(s) of files to run a workflow with', action='append')
    parser.add_argument('--deciderImpl', help='The name of a Python class that contains the implementation of the decider', default='basicDeciderImpl')
    parser.add_argument('wfAccession', help='The sw_accession of the workflow that we wish to run', type=int)
    parser.add_argument('--extraHelp', help='Display extended help for workflow options when wfAccession is specified', action='store_true')
    
    # parse just known arguments and ignore arguments that may be for the workflow
    args = parser.parse_known_args()[0]        
    print(args)
    _validateArguments(args, supportedConstraints)
    ## for debugging print the arguments
    print(args)
    
    # try to dynamically create new options based on a particular workflow
    temporaryDirectory = tempfile.TemporaryDirectory()
    output = subprocess.check_output(["seqware","workflow","ini","--accession",str(args.wfAccession),"--out",temporaryDirectory.name + "/workflow.ini"], universal_newlines=True)
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
    parser.parse_args()
    
    # print out arguments for second pass
    print("Decider framework invoked with:")
    print(args)
    reportFile = _queryWSForProvenanceReport(args, temporaryDirectory, deciderImpl)
    
    # read the provenance report and create a data structure for workflow developers
    # file report will be a multimap with group_col -> column from files report -> value
    filesReport = collections.defaultdict(list)
    with open(reportFile) as csvfile:
        csvfile_reader = csv.DictReader(csvfile, delimiter='\t')
        for row in csvfile_reader:
            print (row)
            # if there is a metatype filter in place, drop out rows that don't match 
            if (args.meta_types is not None and row["file_meta_type"] not in args.meta_types):
                print("Skipping since " + row["file_meta_type"] + " not in " + str(args.meta_types))
                continue
            filesReport[row["group_col"]].append(row)

    # main action of the decider, here we go through each group and examine it for possible launching
    for item in filesReport.items():
        print("Examining group: " + item[0] + " with length " + str(len(item[1])))
        for dict in item[1]:
            print("\t " + dict["file_path"])
    

        
''' queries the file provenance report '''   
def _queryWSForProvenanceReport(args, temporaryDirectory, deciderImpl):
     # figure out version of seqware on the path
    output = subprocess.check_output(["seqware","--version"], universal_newlines=True)
    seqwareVersion = output.rpartition(' ')[2].strip()
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
                print(value)
                if value is not None:
                    specificConstraintDetected = True
            except AttributeError:
                pass
    
    ##TODO: add check here to make sure --all cannot be specified at the same time as any other constraint
    if args.all and specificConstraintDetected:
        print( "Cannot specify --all in combination with " + constraint )
        sys.exit(-1)
    if not args.all and not specificConstraintDetected: 
        print( "Need to specify --all or some other constraint" )
        sys.exit(-1)
        
    
_main()
