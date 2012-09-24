package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingLanesDAO;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingLanes;

public interface ProcessingLanesService {

  public abstract void setProcessingLanesDAO(ProcessingLanesDAO dao);

  public abstract ProcessingLanes findByProcessingLane(Processing processing, Lane lane);

  public abstract void delete(ProcessingLanes processingLanes);

  public abstract void update(ProcessingLanes processingLanes);

  public abstract void insert(ProcessingLanes processingLanes);

  public abstract ProcessingLanes updateDetached(ProcessingLanes processingLanes);
  
  public List<ProcessingLanes> list();

}