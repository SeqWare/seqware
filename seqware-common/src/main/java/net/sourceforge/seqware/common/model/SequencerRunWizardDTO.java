package net.sourceforge.seqware.common.model;							// -*- tab-width: 4 -*-


/* DTO == Data{Transfer,Temporary}Object ??
 * Note that the xDTO subclass adds the extra confirmation fields for
 * implementing the create/update forms which are not stored into the database
 * and hence require this seperate class to implement the form.
 */
public class SequencerRunWizardDTO extends SequencerRun {
	private static final long serialVersionUID = 8465486434105955512L;

	private Registration	domainObject;
	private int laneCount = 8;
	private String strLaneCount;

	public SequencerRunWizardDTO() {
		super();
	}
	
	public int getLaneCount() {
		return laneCount;
	}

	public void setLaneCount(int laneCount) {
		this.laneCount = laneCount;
	}

	public Registration getDomainObject() {
		return domainObject;
	}

	public void setDomainObject(Registration domainObject) {
		this.domainObject = domainObject;
	}

	public String getStrLaneCount() {
		return strLaneCount;
	}

	public void setStrLaneCount(String strLaneCount) {
		this.strLaneCount = strLaneCount;
	}
}

// ex:sw=4:ts=4:
