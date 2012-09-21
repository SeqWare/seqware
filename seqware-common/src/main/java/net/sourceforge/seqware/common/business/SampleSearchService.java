package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.model.SampleSearch;

public interface SampleSearchService {

  public List<SampleSearch> list();

  public SampleSearch findById(Integer id);

  public Integer create(SampleSearch sampleSearch);
}
