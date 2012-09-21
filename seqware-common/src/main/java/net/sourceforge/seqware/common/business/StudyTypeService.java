package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;

public interface StudyTypeService {
  public static final String NAME = "StudyTypeService";

  public void setStudyTypeDAO(StudyTypeDAO studyTypeDAO);

  public void insert(StudyType studyType);

  public void update(StudyType studyType);

  public List<StudyType> list();
  
  public List<StudyType> list(Registration registration);

  public StudyType findByName(String name);

  public StudyType findByID(Integer expID);

  StudyType updateDetached(StudyType studyType);
}

// ex:sw=4:ts=4:
