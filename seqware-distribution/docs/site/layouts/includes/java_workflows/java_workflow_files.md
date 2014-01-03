<pre><code>#!java
    @Override
    public Map<String, SqwFile> setupFiles() {
      try {
        // register an plaintext input file using the information from the INI
        // provisioning this file to the working directory will be the first step in the workflow
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(getProperty("input_file"));
        file0.setType("text/plain");
        file0.setIsInput(true);

      } catch (Exception ex) {
        ex.printStackTrace();
        System.exit(1);
      }
      return this.getFiles();
    }

</code></pre>
