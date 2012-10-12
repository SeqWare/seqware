package net.sourceforge.solexatools.webapp.metamodel;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.SequencerRun;


/**
 * <p>SampleDetailsLineItem class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleDetailsLineItem {

	private SequencerRun sequencerRun;
	private Lane lane;
	private IUS ius;
	
	/**
	 * <p>Constructor for SampleDetailsLineItem.</p>
	 *
	 * @param sr a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
	 */
	public SampleDetailsLineItem(SequencerRun sr, Lane lane, IUS ius){
		this.sequencerRun = sr;
		this.lane = lane;
		this.ius = ius;
	}
	
	/**
	 * <p>Getter for the field <code>sequencerRun</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
	 */
	public SequencerRun getSequencerRun() {
		return sequencerRun;
	}
	
	/**
	 * <p>Getter for the field <code>lane</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
	 */
	public Lane getLane() {
		return lane;
	}
	
	/**
	 * <p>Getter for the field <code>ius</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
	 */
	public IUS getIus() {
		return ius;
	}
}
