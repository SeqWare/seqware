#! /usr/bin/env python3
import subprocess, sys, argparse

parser = argparse.ArgumentParser(description='Transition workflow runs contained in a report to a different state(cancel or retry)')
parser.add_argument('transition', default="retry", type=str, choices=['cancel', 'retry'], help='Transition workflow runs by either invoking "cancel" or "retry" on them')
parser.add_argument('--targetState', default="running", type=str, choices=['submitted','pending','running','failed', 'cancelled'], help='Manipulate workflows in this state')
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument('--input', default=None, help="Workflow run report to read workflow run SWIDs from")
group.add_argument('--output', default="saved_workflow_report.txt", help="Workflow run report to save from CLI")

args = parser.parse_args()

outputFile = args.output
inputFile = args.input
transition = args.transition
targetState = args.targetState

        
output = ""
if(inputFile is None):    
    output = subprocess.check_output(["seqware","workflow","report","--status", targetState, "--tsv"], universal_newlines=True)
    # output copy in case we want to restore
    f = open(outputFile, 'w')
    f.write(output)
    print("Wrote saved workflow run report to {0}".format(outputFile))
    f.close()
else:
    # read from input file
    f = open(inputFile, 'r')
    output = f.read()

outputLines = output.rstrip().splitlines()
workflowRunSWIDs = []
workflowRunColumnIndex = outputLines[0].split('\t').index("Workflow Run SWID")
print("Workflow Run Column found at: {0}".format(workflowRunColumnIndex))
first = True 
for line in outputLines:
    if first :
        first = False
        continue
    val = line.split('\t')[workflowRunColumnIndex];
    print(val);
    workflowRunSWIDs.append(val);

if len(workflowRunSWIDs) == 0:
    print("No workflow runs found")
    sys.exit()

output = subprocess.check_output(["seqware","workflow-run",transition,"--accession",','.join(workflowRunSWIDs)], universal_newlines=True)
print(output)
