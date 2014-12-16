<pre><code>#!java
    @Override
    public void buildWorkflow() {

        // a simple bash job to call mkdir
	// note that this job uses the system's mkdir (which depends on the system being *nix)
	// this also translates into a 3000 h_vmem limit when using sge 
        Job mkdirJob = this.getWorkflow().createBashJob("bash_mkdir").setMaxMemory("3000");
        mkdirJob.getCommand().addArgument("mkdir test1");      
       
	String inputFilePath = this.getFiles().get("file_in_0").getProvisionedPath();
	 
        // a simple bash job to cat a file into a test file
	// the file is not saved to the metadata database
        Job copyJob1 = this.getWorkflow().createBashJob("bash_cp").setMaxMemory("3000");
        copyJob1.setCommand(catPath + " " + inputFilePath + "> test1/test.out");
        copyJob1.addParent(mkdirJob);
	// this will annotate the processing event associated with the cat of the file above
        copyJob1.getAnnotations().put("command.annotation.key.1", "command.annotation.value.1");
        copyJob1.getAnnotations().put("command.annotation.key.2", "command.annotation.value.2");
        
        // a simple bash job to echo to an output file and concat an input file
	// the file IS saved to the metadata database
        Job copyJob2 = this.getWorkflow().createBashJob("bash_cp").setMaxMemory("3000");
	copyJob2.getCommand().addArgument(echoPath).addArgument(greeting).addArgument(" > ").addArgument("dir1/output");
	copyJob2.getCommand().addArgument(";");
	copyJob2.getCommand().addArgument(catPath + " " +inputFilePath+ " >> dir1/output");
        copyJob2.addParent(mkdirJob);
	SqwFile outputFile = createOutputFile("dir1/output", "txt/plain", manualOutput);
        // this will annotate the processing event associated with copying your output file to its final location
        outputFile.getAnnotations().put("provision.file.annotation.key.1", "provision.annotation.value.1");
        copyJob2.addFile(outputFile);

    }

</code></pre>
