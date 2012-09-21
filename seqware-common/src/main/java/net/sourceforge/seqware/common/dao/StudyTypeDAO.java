package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;

public interface StudyTypeDAO {
  public void insert(StudyType sequencerRun);

  public void update(StudyType sequencerRun);

  public List<StudyType> list(Registration registration);

  public StudyType findByName(String name);

  public StudyType findByID(Integer studyTypeID);

  public StudyType updateDetached(StudyType studyType);
  
  public List<StudyType> list();
}
