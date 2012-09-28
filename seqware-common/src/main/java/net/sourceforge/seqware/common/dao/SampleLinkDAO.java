package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleLink;

public interface SampleLinkDAO {

  public abstract void insert(SampleLink sampleLink);

  public abstract void update(SampleLink sampleLink);

  public abstract void delete(SampleLink sampleLink);

  @SuppressWarnings("unchecked")
  public abstract List<SampleLink> findAll(Sample sample);
  
  public List<SampleLink> list();

}