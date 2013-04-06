<pre><code>#!java
    @Override
    public Map<String, SqwFile> setupFiles() {
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");
        file0.setType("text/plain");
        file0.setIsInput(true);
                
        SqwFile file1 = this.createFile("file_out");
        file1.setSourcePath("dir1/output");
        file1.setType("text/plain");
        file1.setIsOutput(true);
        file1.setForceCopy(true);
        
        return this.getFiles();
    }
</code></pre>
