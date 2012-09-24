package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingIUSService;
import net.sourceforge.seqware.common.dao.ProcessingIUSDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingIus;

public class ProcessingIUSServiceImpl implements ProcessingIUSService {

  private ProcessingIUSDAO dao;

  @Override
  public void setProcessingIUSDAO(ProcessingIUSDAO dao) {
    this.dao = dao;
  }

  @Override
  public ProcessingIus findByProcessingIUS(Processing processing, IUS ius) {
    return dao.findByProcessingIUS(processing, ius);
  }

  @Override
  public void delete(ProcessingIus processingIus) {
    dao.delete(processingIus);
  }

  @Override
  public void update(ProcessingIus processingIus) {
    dao.update(processingIus);
  }

  @Override
  public void insert(ProcessingIus processingIus) {
    dao.insert(processingIus);
  }

  @Override
  public ProcessingIus updateDetached(ProcessingIus processingIus) {
    return dao.updateDetached(processingIus);
  }

    @Override
    public List<ProcessingIus> list() {
        return dao.list();
    }
}
