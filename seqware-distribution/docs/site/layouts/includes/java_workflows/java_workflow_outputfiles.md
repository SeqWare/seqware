<pre><code>#!java
    @Override
    public void buildWorkflow() {
	...

	Job copyJob2 = this.getWorkflow().createBashJob("bash_cp");
	...
        copyJob2.addFile(createOutputFile("dir1/output", "txt/plain", manualOutput));
    }

    private SqwFile createOutputFile(String workingPath, String metatype, boolean manualOutput) {
    // register an output file
        SqwFile file1 = new SqwFile();
        file1.setSourcePath(workingPath);
        file1.setType(metatype);
        file1.setIsOutput(true);
        file1.setForceCopy(true);

        // if manual_output is set in the ini then use it to set the destination of this file
        if (manualOutput) {
            file1.setOutputPath(this.getMetadata_output_file_prefix() + getMetadata_output_dir() + "/" + workingPath);
        } else {
            file1.setOutputPath(this.getMetadata_output_file_prefix() + getMetadata_output_dir() + "/"
                + this.getName() + "_" + this.getVersion() + "/" + this.getRandom() + "/" + workingPath);
        }
        return file1;
    }


</code></pre>
