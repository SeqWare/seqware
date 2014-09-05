#! /usr/bin/env python3
import subprocess, sys, argparse, importlib
from builtins import int

###################################################################
## This is an implementation of a 
## decider framework. This class is not designed for inheritance.
###################################################################

def __main():

    parser = argparse.ArgumentParser(description='Run a SeqWare decider in order to schedule workflow runs')
    targetGroup = parser.add_mutually_exclusive_group(required=True)
    bigTargetGroup = targetGroup.add_mutually_exclusive_group()
    bigTargetGroup.add_argument('--all', help='Target the decider at all data in your SeqWare provenance report', action='store_true')
    smallTargetGroup = targetGroup.add_mutually_exclusive_group()
    supportedConstraints = ['study-name', 'lane-SWID', 'ius-SWID', 'sample-name', 'root-sample-name', 'sequencer-run-name', 'organism', 'processing-SWID']
    for constraint in supportedConstraints:
        smallTargetGroup.add_argument('--'+ constraint, help='Target the decider at a ' + constraint + ', can be specified multiple times', action='append')
    parser.add_argument('--test', help='Output commands for scheduling potential workflow runs instead of actually scheduling them', action='store_true')
    parser.add_argument('--host', help='Schedule onto a particular host')
    parser.add_argument('--launch-max', help='The maximum number of jobs to launch at once', default=sys.maxsize, type=int)
    parser.add_argument('--meta-types', help='The meta-type(s) of files to run a workflow with', action='append')
    parser.add_argument('--deciderImpl', help='The name of a Python class that contains the implementation of the decider', default='basicDeciderImpl')
    
    
    args = parser.parse_args()
    ## for debugging print the arguments
    print(args)
    
    decider = importlib.import_module(args.deciderImpl)
    Decider = getattr(decider, 'Decider')
    
    deciderImpl = Decider()
    deciderImpl.init(parser)
    
    
    
    
    
__main()