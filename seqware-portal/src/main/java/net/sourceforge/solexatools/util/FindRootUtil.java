package net.sourceforge.solexatools.util;

import java.util.Set;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>FindRootUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FindRootUtil {
	
	/**
	 * <p>getStudy.</p>
	 *
	 * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
	 * @return a {@link net.sourceforge.seqware.common.model.Study} object.
	 */
	public static Study getStudy(Experiment experiment){
		Study study = experiment.getStudy();
		return study;
	}
	
	/**
	 * <p>getStudy.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @return a {@link net.sourceforge.seqware.common.model.Study} object.
	 */
	public static Study getStudy(Sample sample){
		Study study = getStudy(sample.getExperiment());
		return study;
	}	
	
	/**
	 * <p>getStudy.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @return a {@link net.sourceforge.seqware.common.model.Study} object.
	 */
	public static Study getStudy(Lane lane){
		Study study = getStudy(lane.getSample());
		return study;
	}
	
	/**
	 * <p>getStudy.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @return a {@link net.sourceforge.seqware.common.model.Study} object.
	 */
	public static Study getStudy(Processing processing){
		//Study study = getStudy(lane.getSample());
		Set<Lane> lanes = processing.getLanes();
		if(lanes != null && lanes.size() > 0){
			Log.info("Lane!=null");
			for (Lane lane : lanes) {
				Log.info("Lane_ID = " + lane.getLaneId());
				return getStudy(lane);
			}
		}else{
			Log.info("Lane==null");
			Set<Processing> parents = processing.getParents();
			for (Processing parent : parents) {
				return getStudy(parent);
			}
		}
		Log.info("RETURN NULL");
		return null;
	}
	
	// FOR SEQUENCER RUN
	/**
	 * <p>getSequencerRun.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
	 */
	public static SequencerRun getSequencerRun(Lane lane){
		SequencerRun sequencerRun = lane.getSequencerRun();
		return sequencerRun;
	}
	
	/**
	 * <p>getSequencerRun.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
	 */
	public static SequencerRun getSequencerRun(Processing processing){
		//Study study = getStudy(lane.getSample());
		Set<Lane> lanes = processing.getLanes();
		if(lanes != null && lanes.size() > 0){
			for (Lane lane : lanes) {
				return getSequencerRun(lane);
			}
		}else{
			Set<Processing> parents = processing.getParents();
			for (Processing parent : parents) {
				return getSequencerRun(parent);
			}
		}
		return null;
	}
	
	// FOR ANALYSIS WORKFLOW
}
