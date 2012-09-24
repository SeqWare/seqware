package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingStudies;
import net.sourceforge.seqware.common.model.Study;

public interface ProcessingStudiesDAO {

  public abstract ProcessingStudies findByProcessingStudy(Processing processing, Study study);

  public abstract void delete(ProcessingStudies processingStudies);

  public abstract void update(ProcessingStudies processingStudies);

  public abstract void insert(ProcessingStudies processingStudies);

  public abstract ProcessingStudies updateDetached(ProcessingStudies processingStudies);
  
  public List<ProcessingStudies> list();

}
