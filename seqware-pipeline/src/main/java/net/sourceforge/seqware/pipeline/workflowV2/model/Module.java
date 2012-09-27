package net.sourceforge.seqware.pipeline.workflowV2.model;

import net.sourceforge.seqware.pipeline.workflowV2.model.Module;

public enum Module {
	Java("java"),
	Perl("perl"),
    Seqware_GenericCommandRunner(
	    "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"), 
	Seqware_ProvisionFiles(
	    "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles");
	

    private String fullname;

    private Module(String fullname) {
	this.fullname = fullname;
    }

    public String getName() {
	return this.fullname;
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
