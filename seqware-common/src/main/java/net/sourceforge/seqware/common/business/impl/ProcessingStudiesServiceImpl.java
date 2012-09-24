package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingStudiesService;
import net.sourceforge.seqware.common.dao.ProcessingStudiesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingStudies;
import net.sourceforge.seqware.common.model.Study;

public class ProcessingStudiesServiceImpl implements ProcessingStudiesService {

  private ProcessingStudiesDAO dao;

  @Override
  public void setProcessingStudiesDAO(ProcessingStudiesDAO dao) {
    this.dao = dao;
  }

  @Override
  public ProcessingStudies findByProcessingStudy(Processing processing, Study study) {
    return dao.findByProcessingStudy(processing, study);
  }

  @Override
  public void delete(ProcessingStudies processingStudies) {
    dao.delete(processingStudies);
  }

  @Override
  public void update(ProcessingStudies processingStudies) {
    dao.update(processingStudies);
  }

  @Override
  public void insert(ProcessingStudies processingStudies) {
    dao.insert(processingStudies);
  }

  @Override
  public ProcessingStudies updateDetached(ProcessingStudies processingStudies) {
    return dao.updateDetached(processingStudies);
  }

    @Override
    public List<ProcessingStudies> list() {
        return dao.list();
    }

}
