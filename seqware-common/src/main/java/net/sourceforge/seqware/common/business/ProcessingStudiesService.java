package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingStudiesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingStudies;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>ProcessingStudiesService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingStudiesService {

  /**
   * <p>setProcessingStudiesDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ProcessingStudiesDAO} object.
   */
  public abstract void setProcessingStudiesDAO(ProcessingStudiesDAO dao);

  /**
   * <p>findByProcessingStudy.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingStudies} object.
   */
  public abstract ProcessingStudies findByProcessingStudy(Processing processing, Study study);

  /**
   * <p>delete.</p>
   *
   * @param processingStudies a {@link net.sourceforge.seqware.common.model.ProcessingStudies} object.
   */
  public abstract void delete(ProcessingStudies processingStudies);

  /**
   * <p>update.</p>
   *
   * @param processingStudies a {@link net.sourceforge.seqware.common.model.ProcessingStudies} object.
   */
  public abstract void update(ProcessingStudies processingStudies);

  /**
   * <p>insert.</p>
   *
   * @param processingStudies a {@link net.sourceforge.seqware.common.model.ProcessingStudies} object.
   */
  public abstract void insert(ProcessingStudies processingStudies);

  /**
   * <p>updateDetached.</p>
   *
   * @param processingStudies a {@link net.sourceforge.seqware.common.model.ProcessingStudies} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingStudies} object.
   */
  public abstract ProcessingStudies updateDetached(ProcessingStudies processingStudies);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ProcessingStudies> list();

}
