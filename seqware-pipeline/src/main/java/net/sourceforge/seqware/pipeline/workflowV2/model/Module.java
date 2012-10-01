package net.sourceforge.seqware.pipeline.workflowV2.model;

import net.sourceforge.seqware.pipeline.workflowV2.model.Module;

public enum Module {
	Java("java", "1.6.0"),
	Perl("perl", "5.14.1"),
	Bash("bash", "1.6.0"),
    Seqware_GenericCommandRunner(
	    "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner",""), 
	Seqware_ProvisionFiles(
	    "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles","");
	

    private String fullname;
    private String version;

    private Module(String fullname, String version) {
    	this.fullname = fullname;
    	this.version = version;
    }

    public String getName() {
    	return this.fullname;
    }
    
    public String getVersion() {
    	return this.version;
    }

    /**
     * return the name ignoreof case
     * 
     * @param input
     * @return
     */
    public static Module valueFrom(String input) {
		for (Module module : Module.values()) {
		    if (module.toString().equalsIgnoreCase(input))
			return module;
		}
		return null;
    }
    
}
