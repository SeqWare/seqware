package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;

public interface SampleAttributeDAO {

  public abstract void insert(SampleAttribute sampleAttribute);

  public abstract void update(SampleAttribute sampleAttribute);

  public abstract void delete(SampleAttribute sampleAttribute);

  @SuppressWarnings("unchecked")
  public abstract List<SampleAttribute> findAll(Sample sample);
  
  public List<SampleAttribute> list();

}