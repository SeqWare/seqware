package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;

public interface SequencerRunDAO {

    public void insert(SequencerRun sequencerRun);

    public void insert(Registration registration, SequencerRun sequencerRun);

    public void insert(SequencerRunWizardDTO sequencerRun);

    public void insert(Registration registration, SequencerRunWizardDTO sequencerRun);

    public void update(SequencerRun sequencerRun);

    public void update(Registration registration, SequencerRun sequencerRun);

    public void delete(SequencerRun sequencerRun);

    public List<SequencerRun> list(Registration registration, Boolean isAsc);

    public SequencerRun findByName(String name);

    public SequencerRun findByID(Integer expID);

    public Integer getProcessedCnt(SequencerRun sequencerRun);

    public Integer getProcessingCnt(SequencerRun sequencerRun);

    public Integer getErrorCnt(SequencerRun sequencerRun);

    public List<Integer> getProcStatuses(SequencerRun sequencerRun);

    public SequencerRun findBySWAccession(Integer swAccession);

    public SequencerRun updateDetached(SequencerRun sequencerRun);
    
    public SequencerRun updateDetached(Registration registration, SequencerRun sequencerRun);

    public List<SequencerRun> findByOwnerID(Integer registrationId);

    public List<SequencerRun> findByCriteria(String criteria, boolean isCaseSens);

    public List<SequencerRun> list();
}
