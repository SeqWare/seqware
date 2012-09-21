package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingRelationshipService;
import net.sourceforge.seqware.common.dao.ProcessingRelationshipDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;

public class ProcessingRelationshipServiceImpl implements ProcessingRelationshipService {

  private ProcessingRelationshipDAO dao;

  @Override
  public void setProcessingRelationshipDAO(ProcessingRelationshipDAO dao) {
    this.dao = dao;
  }

  @Override
  public ProcessingRelationship findByProcessings(Processing processingParent, Processing processingChild) {
    return dao.findByProcessings(processingParent, processingChild);
  }

  @Override
  public void delete(ProcessingRelationship processingRelationship) {
    dao.delete(processingRelationship);
  }

  @Override
  public void update(ProcessingRelationship processingRelationship) {
    dao.update(processingRelationship);
  }

  @Override
  public void insert(ProcessingRelationship processingRelationship) {
    dao.insert(processingRelationship);
  }

  @Override
  public ProcessingRelationship updateDetached(ProcessingRelationship processingRelationship) {
    return dao.updateDetached(processingRelationship);
  }

    @Override
    public List<ProcessingRelationship> list() {
        return dao.list();
    }

}
