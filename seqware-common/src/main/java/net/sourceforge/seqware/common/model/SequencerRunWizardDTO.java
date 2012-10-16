package net.sourceforge.seqware.common.model;							// -*- tab-width: 4 -*-


/* DTO == Data{Transfer,Temporary}Object ??
 * Note that the xDTO subclass adds the extra confirmation fields for
 * implementing the create/update forms which are not stored into the database
 * and hence require this seperate class to implement the form.
 */
/**
 * <p>SequencerRunWizardDTO class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunWizardDTO extends SequencerRun {
	private static final long serialVersionUID = 8465486434105955512L;

	private Registration	domainObject;
	private int laneCount = 8;
	private String strLaneCount;

	/**
	 * <p>Constructor for SequencerRunWizardDTO.</p>
	 */
	public SequencerRunWizardDTO() {
		super();
	}
	
	/**
	 * <p>Getter for the field <code>laneCount</code>.</p>
	 *
	 * @return a int.
	 */
	public int getLaneCount() {
		return laneCount;
	}

	/**
	 * <p>Setter for the field <code>laneCount</code>.</p>
	 *
	 * @param laneCount a int.
	 */
	public void setLaneCount(int laneCount) {
		this.laneCount = laneCount;
	}

	/**
	 * <p>Getter for the field <code>domainObject</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public Registration getDomainObject() {
		return domainObject;
	}

	/**
	 * <p>Setter for the field <code>domainObject</code>.</p>
	 *
	 * @param domainObject a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public void setDomainObject(Registration domainObject) {
		this.domainObject = domainObject;
	}

	/**
	 * <p>Getter for the field <code>strLaneCount</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStrLaneCount() {
		return strLaneCount;
	}

	/**
	 * <p>Setter for the field <code>strLaneCount</code>.</p>
	 *
	 * @param strLaneCount a {@link java.lang.String} object.
	 */
	public void setStrLaneCount(String strLaneCount) {
		this.strLaneCount = strLaneCount;
	}
}

// ex:sw=4:ts=4:
