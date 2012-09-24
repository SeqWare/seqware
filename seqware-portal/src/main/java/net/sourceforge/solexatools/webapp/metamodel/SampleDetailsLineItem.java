package net.sourceforge.solexatools.webapp.metamodel;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.SequencerRun;


public class SampleDetailsLineItem {

	private SequencerRun sequencerRun;
	private Lane lane;
	private IUS ius;
	
	public SampleDetailsLineItem(SequencerRun sr, Lane lane, IUS ius){
		this.sequencerRun = sr;
		this.lane = lane;
		this.ius = ius;
	}
	
	public SequencerRun getSequencerRun() {
		return sequencerRun;
	}
	
	public Lane getLane() {
		return lane;
	}
	
	public IUS getIus() {
		return ius;
	}
}
