package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingStudiesService;
import net.sourceforge.seqware.common.dao.ProcessingStudiesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingStudies;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>ProcessingStudiesServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingStudiesServiceImpl implements ProcessingStudiesService {

  private ProcessingStudiesDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingStudiesDAO(ProcessingStudiesDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingStudies findByProcessingStudy(Processing processing, Study study) {
    return dao.findByProcessingStudy(processing, study);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingStudies processingStudies) {
    dao.delete(processingStudies);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingStudies processingStudies) {
    dao.update(processingStudies);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingStudies processingStudies) {
    dao.insert(processingStudies);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingStudies updateDetached(ProcessingStudies processingStudies) {
    return dao.updateDetached(processingStudies);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingStudies> list() {
        return dao.list();
    }

}
