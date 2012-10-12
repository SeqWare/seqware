package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.SampleSearchService;
import net.sourceforge.seqware.common.dao.SampleSearchDAO;
import net.sourceforge.seqware.common.model.SampleSearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
/**
 * <p>SampleSearchServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Service
public class SampleSearchServiceImpl implements SampleSearchService {

  @Autowired
  private SampleSearchDAO sampleSearchDao;

  /** {@inheritDoc} */
  @Override
  public List<SampleSearch> list() {
    return sampleSearchDao.list();
  }

  /** {@inheritDoc} */
  @Override
  public SampleSearch findById(Integer id) {
    return sampleSearchDao.findById(id);
  }

  /** {@inheritDoc} */
  @Override
  public Integer create(SampleSearch sampleSearch) {
    return sampleSearchDao.create(sampleSearch);
  }

}
