package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.SequencerRunDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;

public interface SequencerRunService {

    public static final String NAME = "SequencerRunService";

    public void setSequencerRunDAO(SequencerRunDAO sequencerRunDAO);

    public void insert(SequencerRun sequencerRun);

    public void insert(Registration registration, SequencerRun sequencerRun);

    public void insert(SequencerRunWizardDTO sequencerRun);

    public void insert(Registration registration, SequencerRunWizardDTO sequencerRun);

    public void update(SequencerRun sequencerRun);

    public void update(Registration registration, SequencerRun sequencerRun);

    public void delete(SequencerRun sequencerRun);

    public List<SequencerRun> list();

    public List<SequencerRun> list(Registration registration);

    public List<SequencerRun> list(Registration registration, Boolean isAsc);

    public SequencerRun findByName(String name);

    public SequencerRun findByID(Integer expID);

    public SequencerRun findBySWAccession(Integer swAccession);

    public List<SequencerRun> findByOwnerID(Integer registrationId);

    public boolean hasNameBeenUsed(String oldName, String newName);

    public List<SequencerRun> setProcCountInfo(List<SequencerRun> list);

    public SequencerRun updateDetached(SequencerRun sequencerRun);
    
    public SequencerRun updateDetached(Registration registration, SequencerRun sequencerRun);

    public List<SequencerRun> findByCriteria(String criteria, boolean isCaseSens);
}

// ex:sw=4:ts=4:
