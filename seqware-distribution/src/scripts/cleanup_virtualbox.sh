#! /bin/bash
cd ~
rm input.txt 
rm keyValue_ref.out 
rm accession.txt 
rm workflow.ini
rm -Rf pegasus-dax/seqware/pegasus/*
rm -Rf pegasus-working/seqware/pegasus/*
rm /tmp/*.ini
rm -R /tmp/1367*-0/
rm -R /tmp/dax*
rm -Rf /tmp/*.log
rm -Rf /tmp/input*out
rm -Rf /tmp/keyValue*txt
rm -Rf /tmp/metadata*out
rm -Rf /tmp/output*txt
rm -Rf /tmp/*.out 
rm -Rf /tmp/workflow*ini
rm -Rf /tmp/xml*
cd workflow-dev/
rm -Rf HelloWorld/
cd ..
cd logs
rm -Rf /usr/tmp/seqware-oozie/oozie/*
hadoop fs -ls /user/seqware
hadoop fs -ls /user/seqware/seqware_workflow
hadoop fs -rm -r -f /user/seqware/seqware_workflow/*
