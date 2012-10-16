package net.sourceforge.seqware.pipeline.workflowV2.model;

/**
 * <p>Module class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public enum Module {
    GenericCommandRunner(
	    "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"), ProvisionFiles(
	    "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles"), ParseVEPsVCF(
	    "ca.on.oicr.seqware.modules.ParseVEPsVCF");

    private String fullname;

    private Module(String fullname) {
	this.fullname = fullname;
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
	return this.fullname;
    }
}
