package net.sourceforge.seqware.pipeline.workflowV2.model;

public enum Module {
    GenericCommandRunner(
	    "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"), ProvisionFiles(
	    "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles"), ParseVEPsVCF(
	    "ca.on.oicr.seqware.modules.ParseVEPsVCF");

    private String fullname;

    private Module(String fullname) {
	this.fullname = fullname;
    }

    public String getName() {
	return this.fullname;
    }
}
