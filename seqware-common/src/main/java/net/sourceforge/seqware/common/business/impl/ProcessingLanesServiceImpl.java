package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingLanesService;
import net.sourceforge.seqware.common.dao.ProcessingLanesDAO;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingLanes;

public class ProcessingLanesServiceImpl implements ProcessingLanesService {

  private ProcessingLanesDAO dao;

  @Override
  public void setProcessingLanesDAO(ProcessingLanesDAO dao) {
    this.dao = dao;
  }

  @Override
  public ProcessingLanes findByProcessingLane(Processing processing, Lane lane) {
    return dao.findByProcessingLane(processing, lane);
  }

  @Override
  public void delete(ProcessingLanes processingLanes) {
    dao.delete(processingLanes);
  }

  @Override
  public void update(ProcessingLanes processingLanes) {
    dao.update(processingLanes);
  }

  @Override
  public void insert(ProcessingLanes processingLanes) {
    dao.insert(processingLanes);
  }

  @Override
  public ProcessingLanes updateDetached(ProcessingLanes processingLanes) {
    return dao.updateDetached(processingLanes);
  }

    @Override
    public List<ProcessingLanes> list() {
        return dao.list();
    }
}
