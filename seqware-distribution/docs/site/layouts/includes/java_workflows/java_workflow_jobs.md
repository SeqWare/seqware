<pre><code>#!java
    @Override
    public void buildWorkflow() {
        Job job00 = this.getWorkflow().createBashJob("bash_mkdir");
        job00.getCommand().addArgument("mkdir test1");      
        
        Job job10 = this.getWorkflow().createBashJob("bash_cp");
        job10.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " test1");
        job10.addParent(job00);
        
        Job job11 = this.getWorkflow().createBashJob("bash_cp");
        job11.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " dir1/output");
        job11.addParent(job00);
               

    }
</code></pre>
