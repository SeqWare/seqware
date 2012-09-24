package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingSamplesService;
import net.sourceforge.seqware.common.dao.ProcessingSamplesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSamples;
import net.sourceforge.seqware.common.model.Sample;

public class ProcessingSamplesServiceImpl implements ProcessingSamplesService {

  private ProcessingSamplesDAO dao;

  @Override
  public void setProcessingSamplesDAO(ProcessingSamplesDAO dao) {
    this.dao = dao;
  }

  @Override
  public ProcessingSamples findByProcessingSample(Processing processing, Sample sample) {
    return dao.findByProcessingSample(processing, sample);
  }

  @Override
  public void delete(ProcessingSamples processingSamples) {
    dao.delete(processingSamples);
  }

  @Override
  public void update(ProcessingSamples processingSamples) {
    dao.update(processingSamples);
  }

  @Override
  public void insert(ProcessingSamples processingSamples) {
    dao.insert(processingSamples);
  }

  @Override
  public ProcessingSamples updateDetached(ProcessingSamples processingSamples) {
    return dao.updateDetached(processingSamples);
  }

    @Override
    public List<ProcessingSamples> list() {
        return dao.list();
    }
}
