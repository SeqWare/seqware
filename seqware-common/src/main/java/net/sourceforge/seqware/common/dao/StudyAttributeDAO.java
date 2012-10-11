package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;

public interface StudyAttributeDAO {

  public abstract void insert(StudyAttribute studyAttribute);

  public abstract void update(StudyAttribute studyAttribute);

  public abstract void delete(StudyAttribute studyAttribute);

  @SuppressWarnings("unchecked")
  public abstract List<StudyAttribute> findAll(Study study);
  
  public List<StudyAttribute> list();

}