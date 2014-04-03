<pre><code>#!java

// showing how to access the workflow bundle install location
file0.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");

// showing how to access a property from the INI file
file0.setSourcePath(getProperty("input_file"));

// showing how to access variables out of the ini file
this.getProperty("output_file_1");
</code></pre>
