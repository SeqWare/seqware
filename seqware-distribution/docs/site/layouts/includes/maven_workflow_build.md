        [seqware@seqwarevm maven-bundles]$ cd helloworld/
        [seqware@seqwarevm helloworld]$ mvn install
        [INFO] Scanning for projects...                                                                                       
        [INFO] ------------------------------------------------------------------------                                       
        [INFO] Building seqware-workflow                                                                                      
        [INFO]    task-segment: [install]                                                                                     
        [INFO] ------------------------------------------------------------------------                                       
        [INFO] [properties:read-project-properties {execution: properties-maven-plugin-execution}]                            
        [debug] execute contextualize                                                                                         
        [INFO] [resources:copy-resources {execution: copy-resources}]                                                         
        [INFO] Using 'UTF-8' encoding to copy filtered resources.                                                             
        [INFO] Copying 3 resources                                                                                            
        [debug] execute contextualize                                                                                         
        [INFO] [resources:resources {execution: default-resources}]                                                           
        [INFO] Using 'UTF-8' encoding to copy filtered resources.    
        ...
        main:
        [INFO] Executed tasks
        [INFO] [antrun:run {execution: chmod-perl}]
        [INFO] Executing tasks

        main:
        [INFO] Executed tasks
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESSFUL
        [INFO] ------------------------------------------------------------------------
        [INFO] Total time: 26 seconds
        [INFO] Finished at: Fri Nov 23 14:48:15 EST 2012
        [INFO] Final Memory: 67M/423M
        [INFO] ------------------------------------------------------------------------
