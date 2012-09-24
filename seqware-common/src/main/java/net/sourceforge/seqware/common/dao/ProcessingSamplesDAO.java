package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSamples;
import net.sourceforge.seqware.common.model.Sample;

public interface ProcessingSamplesDAO {

  @SuppressWarnings("rawtypes")
  public abstract ProcessingSamples findByProcessingSample(Processing processing, Sample sample);

  public abstract void delete(ProcessingSamples processingSamples);

  public abstract void update(ProcessingSamples processingSamples);

  public abstract void insert(ProcessingSamples processingSamples);

  public abstract ProcessingSamples updateDetached(ProcessingSamples processingSamples);

  public List<ProcessingSamples> list();
}