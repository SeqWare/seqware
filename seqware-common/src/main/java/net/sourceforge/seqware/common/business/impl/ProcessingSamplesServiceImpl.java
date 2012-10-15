package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingSamplesService;
import net.sourceforge.seqware.common.dao.ProcessingSamplesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSamples;
import net.sourceforge.seqware.common.model.Sample;

/**
 * <p>ProcessingSamplesServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingSamplesServiceImpl implements ProcessingSamplesService {

  private ProcessingSamplesDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingSamplesDAO(ProcessingSamplesDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingSamples findByProcessingSample(Processing processing, Sample sample) {
    return dao.findByProcessingSample(processing, sample);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingSamples processingSamples) {
    dao.delete(processingSamples);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingSamples processingSamples) {
    dao.update(processingSamples);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingSamples processingSamples) {
    dao.insert(processingSamples);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingSamples updateDetached(ProcessingSamples processingSamples) {
    return dao.updateDetached(processingSamples);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingSamples> list() {
        return dao.list();
    }
}
