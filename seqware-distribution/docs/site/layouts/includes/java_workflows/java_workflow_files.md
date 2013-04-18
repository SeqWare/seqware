<pre><code>#!java
    @Override
    public Map<String, SqwFile> setupFiles() {

      try {

        // register an input file
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");
        file0.setType("text/plain");
        file0.setIsInput(true);

        // register an output file
        SqwFile file1 = this.createFile("file_out");
        file1.setSourcePath("dir1/output");
        file1.setType("text/plain");
        file1.setIsOutput(true);
        file1.setForceCopy(true);
        // if output_file is set in the ini then use it to set the destination of this file
        if (hasPropertyAndNotNull("output_file")) { file1.setOutputPath(getProperty("output_file")); }
        return this.getFiles();

      } catch (Exception ex) {
        Logger.getLogger(WorkflowClient.class.getName()).log(Level.SEVERE, null, ex);
        return(null);
      }

    }
</code></pre>
