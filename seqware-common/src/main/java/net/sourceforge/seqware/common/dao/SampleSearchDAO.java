package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.SampleSearch;

public interface SampleSearchDAO {

  public List<SampleSearch> list();

  public SampleSearch findById(Integer id);

  public Integer create(SampleSearch sampleSearch);

}
