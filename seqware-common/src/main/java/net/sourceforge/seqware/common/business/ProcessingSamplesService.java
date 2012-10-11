package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingSamplesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSamples;
import net.sourceforge.seqware.common.model.Sample;

public interface ProcessingSamplesService {

  public abstract void setProcessingSamplesDAO(ProcessingSamplesDAO dao);

  public abstract ProcessingSamples findByProcessingSample(Processing processing, Sample sample);

  public abstract void delete(ProcessingSamples processingSamples);

  public abstract void update(ProcessingSamples processingSamples);

  public abstract void insert(ProcessingSamples processingSamples);

  public abstract ProcessingSamples updateDetached(ProcessingSamples processingSamples);
  
  public List<ProcessingSamples> list();

}