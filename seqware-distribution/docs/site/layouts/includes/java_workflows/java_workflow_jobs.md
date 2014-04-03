<pre><code>#!java
    @Override
    public void buildWorkflow() {

        // a simple bash job to call mkdir
        // note that this job uses the system's mkdir (which depends on the system being *nix)
        Job mkdirJob = this.getWorkflow().createBashJob("bash_mkdir");
        mkdirJob.getCommand().addArgument("mkdir test1");

        String inputFilePath = this.getFiles().get("file_in_0").getProvisionedPath();

        // a simple bash job to cat a file into a test file
        // the file is not saved to the metadata database
        Job copyJob1 = this.getWorkflow().createBashJob("bash_cp");
        copyJob1.setCommand(catPath + " " + inputFilePath + "> test1");
        copyJob1.addParent(mkdirJob);

        // a simple bash job to echo to an output file and concat an input file
        // the file IS saved to the metadata database
        Job copyJob2 = this.getWorkflow().createBashJob("bash_cp");
        copyJob2.getCommand().addArgument(echoPath).addArgument(greeting).addArgument(" > ").addArgument("dir1/output");
        copyJob2.getCommand().addArgument(";");
        copyJob2.getCommand().addArgument(catPath + " " +inputFilePath+ " >> dir1/output");
        copyJob2.addParent(mkdirJob);
        copyJob2.addFile(createOutputFile("dir1/output", "txt/plain", manualOutput));

    }

</code></pre>
